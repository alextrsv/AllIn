package alex.controllers;

import alex.dto.MessengerDto;
import alex.dto.Response;
import alex.dto.ResponseStatus;
import alex.entity.Category;
import alex.entity.Messenger;
import alex.entity.User;
import alex.service.CategoryService;
import alex.service.MessengerService;
import alex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessengerService messengerService;

    @Autowired
    private CategoryService categoryService;



    @GetMapping(produces = "application/json")
    @ResponseBody
    public Iterable<User> allUsers() {
        return userService.getAll();
    }


    @PostMapping("/auth")
    @ResponseBody
    public User addUser(@RequestHeader("Authorization") String token, @RequestBody User newUser){
        return userService.addUser(token, newUser);
    }

    @PostMapping("/downloadProfile")
    @ResponseBody
    public User downloadProfile(@RequestHeader("Authorization") String token){
        return userService.getByToken(token);
    }


    @DeleteMapping("/delete")
    public String removeUser(@RequestHeader("Authorization") String token){
        return userService.delete(token);
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
    public Iterable<Messenger> getUsersMess(@RequestHeader("Authorization") String token, HttpServletResponse response) {
        try {
            return userService.getUsersMess(token);
        }catch (NullPointerException nullPointerException){
            try {
                response.sendRedirect("/users/no-user-error");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    @PostMapping("/messengers/add")
    public String addMessenger(@RequestHeader("Authorization") String token, @RequestHeader("accessToken") String accessToken, @RequestBody MessengerDto mess) {
        try {
           userService.addMessenger(token, accessToken, mess);
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
        messengerService.removeMessenger(token, messengersList);
        return "redirect:/users";
    }


    @PostMapping("/msg-token")
    @ResponseBody
    private Response setMsgToken(@RequestHeader("Authorization") String token,
                                 @RequestHeader(name = "msgToken") String msgToken){

        return userService.setMsgToken(token, msgToken);
    }

    /////////////////////////PART_________2

    @PostMapping("/messengers/change-pos")
    @ResponseBody
    public Response changePosition(@RequestHeader("Authorization") String token,
                                 @RequestBody MessengerDto infMessenger){

        return messengerService.changePosition(token, infMessenger);
    }

    /////////////CATEGORIES

    @GetMapping("/categories")
    @ResponseBody
    public Iterable<Category> getUsersCategories(@RequestHeader("Authorization") String token,
                                                 HttpServletResponse response) {

        return userService.getByToken(token).getCategories();

    }

    @PostMapping("/categories/add")
    @ResponseBody
    public Category createCategory(@RequestHeader("Authorization") String token,
                                   @RequestBody Category dtoCategory){

       return categoryService.createCategory(token, dtoCategory);
    }

    @DeleteMapping("/categories/{id}/delete")
    @ResponseBody
    public Response deleteCategory(@RequestHeader("Authorization") String token, @PathVariable("id") int categoryId){
        return categoryService.delete(token, categoryId);
    }

    @PostMapping("/categories/update")
    @ResponseBody
    public Category updateCategory(@RequestHeader("Authorization") String token, @RequestBody Category infCategory){

      return categoryService.update(token, infCategory);
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
