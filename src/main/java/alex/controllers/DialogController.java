package alex.controllers;

import alex.dto.DialogCategoryHolder;
import alex.dto.Response;
import alex.entity.Category;
import alex.entity.Dialog;
import alex.entity.User;
import alex.service.DialogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class DialogController {

    @Autowired
    DialogService dialogService;

    @GetMapping("/dialog/{id}")
    @ResponseBody
    private Dialog getDialog(@PathVariable(name = "id") int id){
        return dialogService.getById(id);
    }


    @PostMapping("dialog/add/{id}")
    @ResponseBody
    private Response createNewDialog(@PathVariable("id") int messId, @RequestHeader("Authorization") String token, @RequestBody User userTo){
        return dialogService.createDialog(messId, token, userTo);
    }


    @PostMapping("/dialogsToCategories")
    @ResponseBody
    private Response addDialogsToCategories(@RequestHeader("Authorization") String token,
                                            @RequestBody DialogCategoryHolder dialogCategoryHolder){
        return dialogService.addDialogsToCategories(token, dialogCategoryHolder);
    }

    @PostMapping("/favorites/dialogs/delete")
    @ResponseBody
    private Response deleteFromCategories(@RequestHeader("Authorization") String token,
                                          @RequestHeader("deleteFromFavourites") boolean deleteFromFavourites,
                                          @RequestBody DialogCategoryHolder dialogCategoryHolder){
        return dialogService.deleteFromCategories(token, deleteFromFavourites, dialogCategoryHolder);
    }


    @PostMapping("/getFavorites")
    @ResponseBody
    private List<Dialog> getFavDialogs(@RequestHeader("Authorization") String token,
                                       @RequestBody Category categoryTr){
        return dialogService.getFavoriteDialogs(token, categoryTr);
    }

}