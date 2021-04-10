package alex.controllers;

import alex.dto.DialogCategoryHolder;
import alex.dto.Response;
import alex.entity.*;
import alex.exceptions.NoSuchDialogException;
import alex.exceptions.NoSuchMesengerOwnedException;
import alex.service.*;
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
    private Dialog getDialog(@PathVariable(name = "id") int id) throws NoSuchDialogException {
        return dialogService.getById(id);
    }


    @PostMapping("dialog/add/{id}")
    @ResponseBody
    private Response createNewDialog(@PathVariable("id") int messId, @RequestHeader("Authorization") String token, @RequestBody User userTo)
            throws NoSuchMesengerOwnedException {
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


