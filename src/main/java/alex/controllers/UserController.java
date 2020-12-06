package alex.controllers;

import alex.entity.Messenger;
import alex.entity.User;
import alex.entity.UsersMessengers;
import alex.service.MessengerService;
import alex.service.UserService;
import alex.service.UsersMessengersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
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
    public List<User> allUsers() {
        return userService.getAll();
    }


//    @PostMapping("/add")
//    public String addUser(@RequestHeader("Authorization") String token, @RequestParam("phone") String phone){
//        User newUser = new User();
//        newUser.setToken(token);
//        newUser.setPhone(phone);
//
//        userService.addUser(newUser);
//
//        return "redirect:/users";
//
//    }

    @PostMapping("/add")
    public String addUser(@RequestHeader("Authorization") String token, @RequestBody User newUser){
        newUser.setToken(token);
        userService.addUser(newUser);
        return "redirect:/users";

    }

    @DeleteMapping("/delete")
    public String removeUser(@RequestHeader("Authorization") String token){
        usersMessengersService.deleteByUserId(userService.getByToken(token).getId());
        userService.delete(token);
        return "redirect:/users";
    }

    @GetMapping("/no-user-error")
    @ResponseBody
    public String noUserError(){
        return "there is no such user";
    }

    @GetMapping("/messenger-already-exists-error")
    @ResponseBody
    public String messengerAlreadyExists(){
        return "this user already has this messenger";
    }



    @GetMapping(value = "/messengers")
    @ResponseBody
    public Iterable<Messenger> userMess(@RequestHeader("Authorization") String token, HttpServletResponse response) {
        try {
            User user = userService.getByToken(token);
            Collection<UsersMessengers> usersMessengersCollection = user.getUsMes();// коллекция свзяей
            Collection<Messenger> usrMessengers = new ArrayList<>();//пустая коллекция для мессенджеров

            for (UsersMessengers usMes : usersMessengersCollection) {//заполнение коллекции мессенджеров из коллекции связей
                usrMessengers.add(usMes.getMessenger());
            }

            Iterable<Messenger> allMessengers = messengerService.getAll();
            for (Messenger mess : allMessengers) {
                mess.setActivated(usrMessengers.contains(mess));
            }
            return allMessengers;
        }catch (java.lang.NullPointerException nullPointerException){

            try {
                response.sendRedirect("/users/no-user-error");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    @PostMapping("/messengers/add")
    public String addMessenger(@RequestHeader("Authorization") String token, @RequestHeader("accessToken") String accessToken,
                               @RequestBody Messenger mess) {

        try {

            User user = userService.getByToken(token);
            Messenger messenger = messengerService.getById(mess.getId());
            UsersMessengers newUsersMessengers = new UsersMessengers();

            newUsersMessengers.setUser(user);
            newUsersMessengers.setMessenger(messenger);
            newUsersMessengers.setAccessToken(accessToken);

            usersMessengersService.editUsersMessengers(newUsersMessengers);
        }catch (RuntimeException e) {
            Throwable rootCause = com.google.common.base.Throwables.getRootCause(e);
            if (rootCause instanceof SQLException) {
                if ("23505".equals(((SQLException) rootCause).getSQLState())) {
                    return "redirect:/users/messenger-already-exists-error";
                }
            }
        }
        return "redirect:/users";
    }



    @DeleteMapping("/messengers/{id}/delete")
    public String removeMessenger(@RequestHeader("Authorization") String token,
                                  @PathVariable("id") int messid) {

        User user = userService.getByToken(token);
        UsersMessengers usersMessengers = usersMessengersService.getByUIdMId(user.getId(), messid);
        usersMessengersService.delete(usersMessengers.getId());

        return "redirect:/users";
    }
}
