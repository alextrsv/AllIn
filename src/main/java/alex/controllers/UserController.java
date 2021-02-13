package alex.controllers;

import alex.dto.MessengerDto;
import alex.dto.Response;
import alex.dto.ResponseStatus;
import alex.entity.Category;
import alex.entity.Messenger;
import alex.entity.User;
import alex.entity.UsersMessengers;
import alex.service.CategoryService;
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

    @Autowired
    private CategoryService categoryService;


    @GetMapping(produces = "application/json")
    @ResponseBody
    public List<User> allUsers() {
        return userService.getAll();
    }


    @PostMapping("/auth")
    @ResponseBody
    public User addUser(@RequestHeader("Authorization") String token, @RequestBody User newUser){
        if (userService.getByToken(token) == null) {
            //пользователя нет, создается новый
            newUser.setToken(token);
            userService.addUser(newUser);
            return newUser;
        }
        else return userService.getByToken(token);
    }

    @PostMapping("/downloadProfile")
    @ResponseBody
    public User downloadProfile(@RequestHeader("Authorization") String token){
        return userService.getByToken(token);
    }


    @DeleteMapping("/delete")
    public String removeUser(@RequestHeader("Authorization") String token){
        usersMessengersService.deleteByUserId(userService.getByToken(token).getId());
        userService.delete(token);
        return "redirect:/users";
    }

    @GetMapping("/no-user-error")
    @ResponseBody
    public Response noUserError(){
        return new Response(ResponseStatus.ERROR, "there is no such user");
    }

    @GetMapping("/messenger-already-exists-error")
    @ResponseBody
    public Response messengerAlreadyExists(){
        return new Response(ResponseStatus.ERROR, "this user already has this messenger");
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
                               @RequestBody MessengerDto mess) {

        try {

            User user = userService.getByToken(token);
            Messenger messenger = messengerService.getById(mess.getId());
            UsersMessengers newUsersMessengers = new UsersMessengers();

            System.out.println("SIZEEEEE"+user.getUsMes().size()+"\n\n\n\n");

            newUsersMessengers.setUser(user);
            newUsersMessengers.setMessenger(messenger);
            newUsersMessengers.setAccessToken(accessToken);
            newUsersMessengers.setPosition(user.getUsMes().size()+1);

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


    @PostMapping("/messengers/delete")
    public String removeMessenger(@RequestHeader("Authorization") String token,
                                  @RequestBody List<Messenger> messengersList) {

        User user = userService.getByToken(token);
        for (Messenger mess: messengersList) {
            UsersMessengers usersMessengers = usersMessengersService.getByUIdMId(user.getId(), mess.getId());
            usersMessengersService.delete(usersMessengers.getId());
        }

        return "redirect:/users";
    }


    @PostMapping("/msg-token")
    @ResponseBody
    private Response setMsgToken(@RequestHeader("Authorization") String token,
                                 @RequestHeader(name = "msgToken") String msgToken){

        try {
            User user = userService.getByToken(token);
            user.setMsgToken(msgToken);
            userService.editUser(user);
        }catch (java.lang.NullPointerException exeption){
            return new Response(ResponseStatus.ERROR, "there isn't such user. Check auth token");
        }
        return new Response(ResponseStatus.SUCCESS, "msgToken has been set successfully set up");
    }


    /////////////////////////PART_________2

    @PostMapping("/messengers/change-pos")
    @ResponseBody
    public Response changePosition(@RequestHeader("Authorization") String token,
                                 @RequestBody MessengerDto infMessenger){

        UsersMessengers usersMessengers =
                usersMessengersService.getByUIdMId(userService.getByToken(token).getId(), infMessenger.getId());

        usersMessengers.setPosition(infMessenger.getPosition());
        usersMessengersService.editUsersMessengers(usersMessengers);

        return new Response(ResponseStatus.SUCCESS, "position has been changed");
    }


    /////////////CATEGORIES

    @GetMapping("/categories")
    @ResponseBody
    public Iterable<Category> getUsersCategories(@RequestHeader("Authorization") String token,
                                                 HttpServletResponse response) {

        User user = userService.getByToken(token);
        Collection<Category> categories = user.getCategories();

        return categories;
    }


    @PostMapping("/categories/add")
    @ResponseBody
    public Category createCategory(@RequestHeader("Authorization") String token,
                                   @RequestBody Category dtoCategory){

        User user = userService.getByToken(token);
        dtoCategory.setUser(user);
        user.getCategories().add(dtoCategory);
        userService.editUser(user);

        return categoryService.findByTitle(dtoCategory.getTitle());
    }


    @DeleteMapping("/categories/{id}/delete")
    @ResponseBody
    public Response deleteCategory(@RequestHeader("Authorization") String token, @PathVariable("id") int categoryId){

        User user = userService.getByToken(token);
        Category category = categoryService.getById(categoryId);
        if(user.getCategories().contains(category)){
            user.getCategories().remove(category);
            categoryService.delete(categoryId);
            userService.editUser(user);
            return new Response(ResponseStatus.SUCCESS, "category has been deleted");
        }
         else return new Response(ResponseStatus.ERROR, "current user doesn't have such category");
    }


    @PostMapping("/categories/update")
    @ResponseBody
    public Category updateCategory(@RequestHeader("Authorization") String token, @RequestBody Category infCategory){

        Category updCategory = categoryService.getById(infCategory.getId());
        updCategory.setTitle(infCategory.getTitle());
        categoryService.editCategory(updCategory);

        return  updCategory;
    }


    @GetMapping("/status")
    @ResponseBody
    private Response getStatus(){
     Response response = new Response();
     response.setStatus(ResponseStatus.SUCCESS);
     response.setComment("действие было сделано");
        return response;
    }

}
