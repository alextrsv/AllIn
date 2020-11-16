package alex.controllers;

import alex.entity.Messenger;
import alex.entity.User;
import alex.entity.UsersMessengers;
import alex.service.MessengerService;
import alex.service.UserService;
import alex.service.UsersMessengersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessengerService messengerService;

    @Autowired
    private UsersMessengersService usersMessengersService;



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
        usersMessengersService.deleteByUserId(userService.getByToken(token).getId());
        userService.delete(token);
        return "redirect:/users";
    }

    @GetMapping(value = "/{id}/messengers")
    @ResponseBody
    public Iterable<Messenger> userMess(@PathVariable(value = "id") int id, Model model) {
        User user = userService.getById(id);
        Collection<UsersMessengers> usersMessengersCollection = user.getUsMes();// коллекция свзяей
        Collection<Messenger> usrMessengers = new ArrayList<>();//пустая коллекция для мессенджеров

        for (UsersMessengers usMes: usersMessengersCollection) {//заполнение коллекции мессенджеров из коллекции связей
            usrMessengers.add(usMes.getMessenger());
        }

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
        Messenger messenger = messengerService.getById(messid);
        UsersMessengers newUsersMessengers = new UsersMessengers();

        newUsersMessengers.setUser(user);
        newUsersMessengers.setMessenger(messenger);
        newUsersMessengers.setAccessToken(Integer.parseInt(accessToken));

        usersMessengersService.editUsersMessengers(newUsersMessengers);


        return "redirect:/users/{id}/messengers";   
    }


    @DeleteMapping("/{id}/messengers/{messid}")//////////////////////////////////////////////////////////////////////////////////////////////
    public String removeMessenger(@PathVariable(value = "id") int id,
                         @RequestHeader("messid") int messid, Model model) {


        User user = userService.getById(id);
        Messenger messenger = messengerService.getById(messid);
        UsersMessengers usersMessengers = usersMessengersService.getByUIdMId(id, messid);
        usersMessengersService.delete(usersMessengersService.getByUIdMId(id, messid).getId());
//        user.getMessengers().remove(messengerService.getById(messid));
//        userService.editUser(user);

        return "redirect:/users/{id}/messengers";
    }

    //ПАСХАЛКА|HEROKU
}
