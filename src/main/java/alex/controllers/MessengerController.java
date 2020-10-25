package alex.controllers;

import alex.entity.Messenger;
import alex.service.MessengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
//@RequestMapping("/messengers")
public class MessengerController {

    @Autowired
    private MessengerService messengerService;

    @GetMapping(value = "messengers", produces = "application/json")
    @ResponseBody
    public List<Messenger> allUsers(Model model) {
        List<Messenger> messengers = messengerService.getAll();
        return messengers;
    }
}
