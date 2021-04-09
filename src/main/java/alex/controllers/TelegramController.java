package alex.controllers;

import alex.ServerApplication;
import alex.entity.DialogToUser;
import alex.handlers.TelegClient;
import alex.model.Dialog;
import alex.model.TelegramMess;
//import alex.service.ChatService;
import alex.service.DialogToUserService;
import alex.service.impl.DialogToUserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.tdlight.common.Init;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.jni.TdApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class TelegramController {

//    @Autowired
//    private ChatService chatService;

    @Autowired
    ObjectMapper mapper;
    public static Map<String, TelegClient> clients = new HashMap<>();
    private int directoryNumber = 11;

    static{
        try {
            Init.start();
        } catch (CantLoadLibrary cantLoadLibrary) {
            cantLoadLibrary.printStackTrace();
        }
    }

    @PostMapping(value = "/telegram_auth", produces = "application/json")
    public ObjectNode telegramAuth(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> object){
//        Gson gson = new Gson();
//        JsonObject jsonObject = gson.fromJson(object, JsonObject.class);
//
//        ServerApplication.logger.info(jsonObject.toString());

//        String phone = object.get("phone").getAsString();
//        int mess_id = object.get("mess_id").getAsIn


        String phone = (String)object.get("phone");
        int mess_id = (Integer)object.get("mess_id");

        ServerApplication.logger.info("telegram_auth" + phone + mess_id);
        try {
            TelegClient client = new TelegClient();
            client.setPhoneNumber(phone);
            ServerApplication.logger.info("token = " + token);
            clients.put(token, client);
            client.createNewClient(directoryNumber);
            client.setToken(token);
            directoryNumber++;
        }catch (Exception ex){
            ServerApplication.logger.info("Authorization failed");
            return mapper.createObjectNode().put("status","error").put("comment", ex.getMessage());

        }
        ServerApplication.logger.info("Authorization is done");
        return mapper.createObjectNode().put("status","success").put("comment", "");
    }

    @PostMapping("/telegram_code")
    public ObjectNode telegramCode(@RequestHeader("Authorization") String token, @RequestBody Map<String, Object> map){
        ServerApplication.logger.info("telegram_code");
        String code = (String)map.get("confirm_code");

        clients.get(token).setCode(code);
        return mapper.createObjectNode().put("status","success").put("comment", "");
    }

    @GetMapping(value = "/telegram_gci/{mess_id}", produces = "application/json")
    public List<Dialog> telegramGetChatsId(@RequestHeader("Authorization") String token, @PathVariable("mess_id") int mess_id){
        ServerApplication.logger.info("telegram_gci " + mess_id);
//        List<Dialog> dialogs = clients.get(token).gci();
//        for (Dialog d:
//             dialogs) {
//            chatService.addChat(d.getId(), token, mess_id);
//        }

        return clients.get(token).gci();
    }

    @PostMapping("/telegram_lo")
    public ObjectNode telegramLogOut(@RequestHeader("Authorization") String token){
        ServerApplication.logger.info("telegram_lo");
        clients.get(token).logOut();
        return mapper.createObjectNode().put("status","success").put("comment", "");
    }

    @GetMapping(value = "/telegram_history/{mess_id}/{chat_id}", produces = "application/json")
    @ResponseBody
    public List<TelegramMess> telegramGetChatHistory(@RequestHeader("Authorization") String token, @PathVariable("chat_id") long chatId){

        List<TelegramMess> list = new ArrayList<>();
        String lastMsgText = "";
        TdApi.Message lastMess = clients.get(token).getHistoryFromChat(chatId, 0, 1)[0];
        try {
            lastMsgText = ((TdApi.MessageText) lastMess.content).text.text;
        }catch(ClassCastException ex){
            lastMsgText = "недопустимый символ";
        }

        list.add(new TelegramMess(lastMess.id, lastMsgText, lastMess.date, clients.get(token).getMessageType(lastMess)));

        System.out.println("Last Message was gotten!");

        TdApi.Message[] messages = clients.get(token).getHistoryFromChat(chatId, 0, 50);
        System.out.println("messages were gotten");
        boolean flag = true;
        for (TdApi.Message message:
                messages) {

            ServerApplication.logger.info(((TdApi.MessageText) message.content).text.text);

            if(flag){
                flag = false;
                continue;
            }

            String text = "";
            try {
                text = ((TdApi.MessageText) message.content).text.text;
            }catch(ClassCastException ex){
                text = "недопустимый символ";
            }

            list.add(new TelegramMess(message.id, text, message.date, clients.get(token).getMessageType(message)));

        }

        return list;
    }

    @Autowired
    DialogToUserService dialogToUserService;

    @GetMapping(value="/test")
    public void test(/*@RequestHeader("Authorization") String token*/){
//        DialogToUserService dialogToUserService = new DialogToUserServiceImpl();

        List<DialogToUser> dialogsToUsers = dialogToUserService.getUsersByChatId(817388954);

        for (DialogToUser d:
             dialogsToUsers) {
            System.out.println(d.getDialog().getId() + " " + d.getUser().getMsgToken());
        }

    }


}
