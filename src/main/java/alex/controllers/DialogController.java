package alex.controllers;

import alex.entity.Dialog;
import alex.entity.DialogToUser;
import alex.service.DialogService;
import alex.service.DialogToUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DialogController {
    @Autowired
    DialogService dialogService;

    @Autowired
    DialogToUserService dialogToUserService;

    @GetMapping("/dialog/{id}")
    @ResponseBody
    private Dialog getDialog(@PathVariable(name = "id") int id){
        return dialogService.getById(id);
    }



}

