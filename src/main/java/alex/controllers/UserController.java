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

    @GetMapping("/form")
    public String form(Model model) {
        return "new";
    }

    @GetMapping("/remove")
    public String remove(Model model) {
        return "delete";
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


    @PostMapping("/{id}/add")
    public String create(@PathVariable(value = "id") int id, @RequestParam("messid") int messid,
                                      @RequestParam("access_token") String accessToken, Model model) {


        User user = userService.getById(id);
        user.getMessengers().add(messengerService.getById(messid));

        userService.editUser(user);

        return "redirect:/users/{id}/messengers";
    }


    @PostMapping("/{id}/remove")
    public String create(@PathVariable(value = "id") int id,
                         @RequestParam("messid") int messid, Model model) {


        User user = userService.getById(id);
        user.getMessengers().remove(messengerService.getById(messid));

        userService.editUser(user);

        return "redirect:/users/{id}/messengers";
    }

}
