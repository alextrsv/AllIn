package alex.controllers;

import alex.ServerApplication;
import alex.entity.Messenger;
import alex.service.MessengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MessengerController {

    @Autowired
    private MessengerService messengerService;

    @GetMapping(value = "messengers", produces = "application/json")
    @ResponseBody
    public Iterable<Messenger> allUsers(Model model) {
        Iterable<Messenger> messengers = messengerService.getAll();

        ServerApplication.logger.info(messengers.toString());
        return messengers;
    }
}
