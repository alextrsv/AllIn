package alex.controllers;

import alex.entity.Messenger;
import alex.entity.User;
import alex.service.MessengerService;
import alex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessengerService messengerService;



    @GetMapping(produces = "application/json")
    @ResponseBody
    public List<User> allUsers(Model model) {
        List<User> users = userService.getAll();
        return users;
    }


    @PostMapping()
    public String AddUser(@RequestHeader("Authorization") String token, @RequestParam("phone") int phone){
        User newUser = new User();
        newUser.setToken(token);
        newUser.setPhone(phone);

        userService.addUser(newUser);

        return "redirect:/users";

    }

    @DeleteMapping()
    public String removeUser(@RequestHeader("Authorization") String token){
        userService.delete(token);
        return "redirect:/users";
    }

    @GetMapping(value = "/{id}/messengers")
    @ResponseBody
    public Iterable<Messenger> userMess(@PathVariable(value = "id") int id, Model model) {
        User user = userService.getById(id);
        Collection<Messenger> usrMessengers = user.getMessengers();
        Iterable<Messenger> allMessengers = messengerService.getAll();
        for (Messenger mess: allMessengers) {
            mess.setActivated(usrMessengers.contains(mess));
        }
        return allMessengers;
    }


    @PostMapping("/{id}/messengers")
    public String addMessenger(@PathVariable(value = "id") int id, @RequestParam("messid") int messid,
                                      @RequestParam("accessToken") String accessToken, Model model) {


        User user = userService.getById(id);
        user.getMessengers().add(messengerService.getById(messid));

        userService.editUser(user);

        return "redirect:/users/{id}/messengers";   
    }


    @DeleteMapping("/{id}/messengers/{messid}")
    public String removeMessenger(@PathVariable(value = "id") int id,
                         @RequestHeader("messid") int messid, Model model) {


        User user = userService.getById(id);
        user.getMessengers().remove(messengerService.getById(messid));

        userService.editUser(user);

        return "redirect:/users/{id}/messengers";
    }

    //ПАСХАЛКА|HEROKU
}
