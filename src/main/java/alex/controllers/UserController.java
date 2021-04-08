package alex.controllers;

import alex.dto.MessengerDto;
import alex.dto.Response;
import alex.entity.Category;
import alex.entity.Messenger;
import alex.entity.User;
import alex.exceptions.*;
import alex.service.CategoryService;
import alex.service.MessengerService;
import alex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;

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
    public User downloadProfile(@RequestHeader("Authorization") String token) throws NoSuchUserException {
        return userService.getByToken(token);
    }


    @DeleteMapping("/delete")
    @ResponseBody
    public Response removeUser(@RequestHeader("Authorization") String token) throws NoSuchUserException {
        try{
            userService.delete(token);
        }catch (NullPointerException nullPointerException) {
            throw new NoSuchUserException(token);
        }
        return new Response("user with " + token + " token has been deleted");
    }


    @GetMapping(value = "/messengers")
    @ResponseBody
    public Iterable<Messenger> getUsersMess(@RequestHeader("Authorization") String token) throws NoSuchUserException{
        return userService.getUsersMess(token);
    }


    @PostMapping("/messengers/add")
    @ResponseBody
    public Response addMessenger(@RequestHeader("Authorization") String token,
                                 @RequestHeader("accessToken") String accessToken, @RequestBody MessengerDto mess)
            throws NoSuchUserException, NoSuchMessengerException, MessengerAlreadyOwnedException {

        try {
            userService.addMessenger(token, accessToken, mess);
        }catch (NoSuchElementException noSuchElementException) {
            throw new NoSuchMessengerException(mess.getId());
        }
        return new Response("messenger '" + mess.getTitle() + "'has been added to user '" + token);
    }

    @PostMapping("/messengers/delete")
    @ResponseBody
    public Response removeMessenger(@RequestHeader("Authorization") String token,
                                    @RequestBody List<Messenger> messengersList) throws NoSuchUserException, NoSuchMesengerOwnedException {

        messengerService.removeMessenger(token, messengersList);


        return new Response("messengers have been deleted from user '" + token);
    }


    @PostMapping("/msg-token")
    @ResponseBody
    private Response setMsgToken(@RequestHeader("Authorization") String token,
                                 @RequestHeader(name = "msgToken") String msgToken) throws NoSuchUserException {

        userService.setMsgToken(token, msgToken);


        return new Response("MSG token has been set to user with token " + token);
    }

//    /////////////////////////PART_________2

    @PostMapping("/messengers/change-pos")
    @ResponseBody
    public Response changePosition(@RequestHeader("Authorization") String token,
                                   @RequestBody MessengerDto infMessenger) throws NoSuchUserException, NoSuchMesengerOwnedException {

        messengerService.changePosition(token, infMessenger);
        return new Response("position has been changed");
    }


//    /////////////CATEGORIES

    @GetMapping("/categories")
    @ResponseBody
    public Iterable<Category> getUsersCategories(@RequestHeader("Authorization") String token) throws NoSuchUserException {
        return userService.getByToken(token).getCategories();
    }

    @PostMapping("/categories/add")
    @ResponseBody
    public Category createCategory(@RequestHeader("Authorization") String token,
                                   @RequestBody Category dtoCategory) throws NoSuchUserException {

        return categoryService.createCategory(token, dtoCategory);
    }

    @DeleteMapping("/categories/{id}/delete")
    @ResponseBody
    public Response deleteCategory(@RequestHeader("Authorization") String token, @PathVariable("id") int categoryId)
            throws NoSuchUserException, NoSuchCategoryOwnedByUserException, NoSuchCategoryException {
        categoryService.delete(token, categoryId);
        return new Response("Category with " + categoryId + "id has been deleted");
    }

    @PostMapping("/categories/update")
    @ResponseBody
    public Category updateCategory(@RequestHeader("Authorization") String token, @RequestBody Category infCategory)
            throws NoSuchCategoryOwnedByUserException, NoSuchUserException, NoSuchCategoryException {

        return categoryService.update(token, infCategory);
    }

}
