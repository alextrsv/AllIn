package alex.controllers;

import alex.dto.DialogCategoryHolder;
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
          3. Удалить диалог из избранного(из категории)
          4. Получить все диалоги из категории пользователя
    */


    @PostMapping("dialog/add/{id}")
    @ResponseBody
    private String createNewDialog(@PathVariable("id") int messId, @RequestHeader("Authorization") String token){

        User user = userService.getByToken(token);

        List<Messenger> messengersOfUser = new ArrayList<Messenger>();
        for (UsersMessengers useMes: user.getUsMes()) {
            messengersOfUser.add(useMes.getMessenger());
        }
        Messenger currentMessenger = messengerService.getById(messId);

        if(!messengersOfUser.contains(currentMessenger))
            return "This user do not have such messenger";
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
            return "OK";
        }
    }


    @PostMapping("/dialogsToCategories")
    @ResponseBody
    private String addDialogsToCategories(@RequestHeader("Authorization") String token,
                                       @RequestBody DialogCategoryHolder dialogCategoryHolder){

        List<Dialog> dialogs = new ArrayList<Dialog>();
        List<Category> categories = new ArrayList<Category>();

        dialogs = dialogCategoryHolder.getDialogs();
        categories = dialogCategoryHolder.getCategories();

        User user = userService.getByToken(token);

        for (Dialog dg: dialogs) {
            Dialog dialog = dialogService.getById(dg.getId());
            DialogToUser dialogToUser = dialogToUserService.getByDidUid(dialog.getId(), user.getId());
            if(dialogToUser == null) return "у пользователя нет диалога c id = " + dialog.getId();
            for (Category ct : categories) {
                Category category = categoryService.getById(ct.getId());
                if(category.getUser().getId() != user.getId()) return "у пользователя нет категории c id = " + category.getId();
                Favorites favorites = new Favorites();

                favorites.setDialogToUser(dialogToUser);
                favorites.setCategory(category);

                favoritesService.edit(favorites);
            }
        }
        return "OK";
    }


//    @GetMapping("/dialogCat")
//    @ResponseBody
//    private DialogCategoryHolder getEx(){
//        DialogCategoryHolder dialogCategoryHolder = new DialogCategoryHolder();
//        dialogCategoryHolder.setDialog(dialogService.getById(4));
//        dialogCategoryHolder.setCategory(categoryService.getById(2));
//
//        return  dialogCategoryHolder;
//    }
}

