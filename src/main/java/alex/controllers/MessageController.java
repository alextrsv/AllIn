package alex.controllers;

import alex.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MessageController {
    public static Map<String, String> tokens = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @PostMapping(value = "/check_token")
    @ResponseBody
    public String checkToken(@RequestHeader("Authorization") String token, @RequestBody Message message){
        logger.info("message controller runs");
        logger.info(message.getSingleUseToken() + " " + token);

        logger.info(tokens.get(message.getSingleUseToken()));
        if(tokens.get(message.getSingleUseToken()).equals(token)){
            tokens.remove(message.getSingleUseToken());
            logger.info("SUCCESS");
            return "{'status' : 'SUCCESS'}";
//            return "SUCCESS";
        }

        tokens.remove(message.getSingleUseToken());
        logger.info("ERROR");
        return "{'status' : 'ERROR'}";
//        return  "ERROR";
    }


}
