package alex.controllers;

import alex.dto.DialogCategoryHolder;
import alex.dto.Response;
import alex.dto.ResponseStatus;
import alex.entity.*;
import alex.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DialogController {
    @Autowired
    DialogService dialogService;

    @Autowired
    DialogToUserService dialogToUserService;

    @Autowired
    UserService userService;

    @Autowired
    MessengerService messengerService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    FavoritesService favoritesService;


    @GetMapping("/dialog/{id}")
    @ResponseBody
    private Dialog getDialog(@PathVariable(name = "id") int id){
        return dialogService.getById(id);
    }
 

    /*
    todo:
          + 1. Создать новый диалог пользователем
          + 2. Добавить диалог в категорию
          + 3. Удалить диалог из избранного(из категории)
          + 4. Получить все диалоги из категории пользователя
    */


    @PostMapping("dialog/add/{id}")
    @ResponseBody
    private Response createNewDialog(@PathVariable("id") int messId, @RequestHeader("Authorization") String token){

        User user = userService.getByToken(token);

        List<Messenger> messengersOfUser = new ArrayList<Messenger>();
        for (UsersMessengers useMes: user.getUsMes()) {
            messengersOfUser.add(useMes.getMessenger());
        }
        Messenger currentMessenger = messengerService.getById(messId);

        if(!messengersOfUser.contains(currentMessenger))
            return new Response(ResponseStatus.ERROR, "This user do not have such messenger");
        else {

            Dialog dialog = new Dialog();
            dialog.setIcon("Icon2");
            dialog.setNote("testing http dialog adding");
            dialog.setTitle("DIALOG2");
            dialog.setMessenger(messengerService.getById(messId));

            DialogToUser dialogToUser = new DialogToUser();

            dialogToUser.setDialog(dialog);
            dialogToUser.setUser(user);

            dialogService.edit(dialog);
            dialogToUserService.edit(dialogToUser);
            return new Response(ResponseStatus.SUCCESS, "New dialog has been created");

        }
    }


    @PostMapping("/dialogsToCategories")
    @ResponseBody
    private Response addDialogsToCategories(@RequestHeader("Authorization") String token,
                                       @RequestBody DialogCategoryHolder dialogCategoryHolder){

        List<Dialog> dialogs = dialogCategoryHolder.getDialogs();
        List<Category> categories = dialogCategoryHolder.getCategories();


        User user = userService.getByToken(token);

        for (Dialog dg: dialogs) {
            Dialog dialog = dialogService.getById(dg.getId());
            DialogToUser dialogToUser = dialogToUserService.getByDidUid(dialog.getId(), user.getId());
            if(dialogToUser == null)
                return new Response(ResponseStatus.ERROR, "у пользователя нет диалога c id = " + dialog.getId());
            for (Category ct : categories) {
                Category category = categoryService.getById(ct.getId());
                if(category.getUser().getId() != user.getId())
                    return new Response(ResponseStatus.ERROR,"у пользователя нет категории c id = " + category.getId());
                Favorites favorites = new Favorites();

                favorites.setDialogToUser(dialogToUser);
                favorites.setCategory(category);

                favoritesService.edit(favorites);
            }
        }
        return new Response(ResponseStatus.SUCCESS, "all dialogs have been added to all categories");
    }

    @PostMapping("/favorites/dialogs/delete")
    @ResponseBody
    private Response deleteFromCategories(@RequestHeader("Authorization") String token,
                                        @RequestHeader("deleteFromFavourites") boolean deleteFromFavourites,
                                        @RequestBody DialogCategoryHolder dialogCategoryHolder){

        List<Dialog> dialogs = dialogCategoryHolder.getDialogs();
        User user = userService.getByToken(token);
        List<DialogToUser> dialogsToUserToDelete = new ArrayList<DialogToUser>();
        for (Dialog dg : dialogs) {
            dialogsToUserToDelete.add(dialogToUserService.getByDidUid(dg.getId(), user.getId()));
        }

        if(deleteFromFavourites) {
            for (DialogToUser dialogToUserToDelete : dialogsToUserToDelete)
                favoritesService.deleteByDialogToUserId(dialogToUserToDelete.getId());
        }
        else{
            List<Category> categories = dialogCategoryHolder.getCategories();
            for (DialogToUser dialogToUserToDelete: dialogsToUserToDelete) {
                for (Category category: categories) {
                    favoritesService.deleteBy2Id(dialogToUserToDelete.getId(), category.getId());
                }
            }
        }

        return new Response(ResponseStatus.SUCCESS, "dialogs have been deleted from categories");
    }


    @PostMapping("/getFavorites")
    @ResponseBody
    private List<Dialog> getFavDialogs(@RequestHeader("Authorization") String token,
                                 @RequestBody Category categoryTr){

        User user = userService.getByToken(token);
        List<DialogToUser> dialogToUserList = (List<DialogToUser>) user.getDialogToUserCollection();
        List<Dialog> dialogs = new ArrayList<Dialog>();

        if(categoryTr.getId() != 0){
            Category category = categoryService.getById(categoryTr.getId());
            for (DialogToUser dialogToUser: dialogToUserList) {
                if(dialogToUser.getFavorites().size() != 0) {
                    for (Favorites fav :dialogToUser.getFavorites()) {
                        if(fav.getCategory().getId() == category.getId()) {
                            dialogs.add(dialogToUser.getDialog());
                            break;
                        }
                    }
                }
            }
        }else{
            for (DialogToUser dialogToUser: dialogToUserList) {
                if(dialogToUser.getFavorites().size() != 0) dialogs.add(dialogToUser.getDialog());
            }
        }

        return dialogs;
    }

}


