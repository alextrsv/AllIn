package alex.handlers;

import alex.ServerApplication;
import alex.controllers.TelegramController;
import alex.entity.DialogToUser;
import alex.entity.User;
import alex.service.DialogService;
import alex.service.DialogToUserService;
import alex.service.UserService;
import com.google.gson.Gson;
import it.tdlight.jni.TdApi;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component

public class SocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);
    private final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890@$#^&?*()}{][%";
    public static Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    @Autowired
    private UserService userService;

    @Autowired
    private DialogService dialogService;

    @Autowired
    private DialogToUserService dialogToUserService;


    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        logger.info("recieve Pong Message");
//        handleMessage(session, message);
        super.handlePongMessage(session, message);
    }

//    @Override
//    public void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException, IOException {
//        logger.info("method handleTextMessage");
//
//
////        String clientMessage = message.getPayload();
////        logger.info(clientMessage);
////        session.sendMessage(new TextMessage(clientMessage));
////        session.getPrincipal().getName();
////        Principal
//
////		Map<String, Object> values = new Gson().fromJson(message.getPayload(), Map.class);
////		logger.info(values.get("senderName").toString());
//
//        String content = message.getPayload();
//        logger.info(content);
//
//        //отправка в виде джейсона
//
////        TdApi.Message message1 = TelegClient.sendMessage((String)session.getAttributes().get("telegramChatId"), content);
//        TdApi.Message message1 = (TelegramController.clients.get((String)session.getAttributes().get("senderId"))).sendMessage((String)session.getAttributes().get("telegramChatId"), content);
//
//        SimpleDateFormat newFormat = new SimpleDateFormat("dd.MM.yyyy");
//
//        //id from
//        JSONObject object = new JSONObject();
//
//        object.put ("message_id", message1.id);
//        ServerApplication.logger.info("message_id" + message1.id);
//
//        object.put ("message_text", content);
//        ServerApplication.logger.info("message_text" + content);
//
//        object.put("message_type", "out");
////        object.put("message_date", newFormat.format(new Date(message1.date)));
//        object.put("message_time", message1.date);
//
////        ServerApplication.logger.info("date: " + newFormat.format(new Date(message1.date)));
//        ServerApplication.logger.info("time: " + message1.date);
//
//        session.sendMessage(new TextMessage(object.toString()));
//        String recipientId = (String) session.getAttributes().get("recipientId");
//
//        String senderId = (String) session.getAttributes().get("senderId");
//        logger.info(senderId + " " + recipientId);
//
//        object.put("message_type", "in");
//
//        try {
//            sessions.get(recipientId).sendMessage(new TextMessage(object.toString()));
//            ServerApplication.logger.info("before sendMessage");
////            TelegClient.sendMessage((String)session.getAttributes().get("telegramChatId"), content);
//        } catch (Exception e) {
//
//            String singleUseToken = generateSingleUseToken(16);
//            logger.info("singleUseToken = " + singleUseToken);
//            logger.info("senderId = " + senderId);
//            MessageController.tokens.put(singleUseToken, senderId);
//
//            Map<String, String> map = new HashMap<>();
//            map.put("notification_token", singleUseToken);
//            map.put("chat_id", "123");
//            map.put("messenger_id", "3");
//
//            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
//
//            //Все параметры берутся из бд
//            pushNotificationRequest.setToken("fhsrKfmWS-61TiRkyGuqtQ:APA91bGEP1MT4p1T2nEoKLsQNn7sqLUgL2eyxHOodqeSW_uZ54Vp5YFEpHOMsMhzekLU0Rv0rb4wfj4XHcF-YsYYfslJa247koqLX335Pc5OapxFWxy1VNVe5i8HEeDsWW81l6F8i2Yo");
//            pushNotificationRequest.setTitle(senderId);
//            pushNotificationRequest.setMessage(content);
//            pushNotificationRequest.setMap(map);
//
////            PushNotificationService pushNotificationService = new PushNotificationService(new FCMService());
//            FCMService service = new FCMService();
//            logger.info("bool = " + service);
//
//            try {
//                service.sendMessageToToken(pushNotificationRequest);
//            } catch (ExecutionException executionException) {
//                logger.error(executionException.getMessage());
//            }
//
//            // title = Vlad Vekshin / chat name
//            // body - само сообщение
//
//            // putData - chatId, messengerId, flag (генерировать одноразовый токен, при нажатии на уведомление
//            // два обработчкиа запросов на отпраку токена и на проверку на соответствия токена из памяти с полученным +
//            // + проверка на uid из текщей сессии польщователя (Authorization))
//            // получение токена
//            // доставать из бд токены от FCM
//
//        }
//    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException, IOException {
//        Map<String, Object> values = new Gson().fromJson(message.getPayload(), Map.class);
        JSONObject object1 = new JSONObject(message.getPayload());
        ServerApplication.logger.info("handleTextMessage method");
        //messageId - это наш сгенерированный id
//        long messageId = (long) values.get("mess_rand_id");
        long messageId = object1.getLong("mess_rand_id");
        String content = object1.getString("mess_text");
        int time = object1.getInt("mess_time");

//        String content = values.get("mess_text").toString();
//        int time = (int) values.get("mess_time");


        ServerApplication.logger.info("mess_rand_id = " + messageId + " content = " + content);
        String senderToken = (String) session.getAttributes().get("senderToken");


        switch ((int) session.getAttributes().get("messengerId")) {
            case 1:
                ServerApplication.logger.info("token1 = " + session.getAttributes().get("senderToken"));

                TdApi.Message message1 = (TelegramController.clients.get((String) session.getAttributes().get("senderToken"))).sendMessage((String) session.getAttributes().get("chatId"), content);

                JSONObject object = new JSONObject();

                object.put("mess_rand_id", messageId);
                object.put("mess_api_id", message1.id);
                object.put("mess_text", content);
                object.put("mess_direct", "out");
                object.put("mess_time", time);


                //Отправка сообщения в сессию отпправителю
                session.sendMessage(new TextMessage(object.toString()));
                TelegramController.clients.get((String) session.getAttributes().get("senderToken")).setIgnore(true);


                //Отправоляем объект Message получателю в сессию
                object.put("mess_direct", "in");

//                User userSender = userService.getByToken((String) session.getAttributes().get("senderToken"));
//                userSender.getDialogToUserCollection()

                List<DialogToUser> dialogsToUsers = dialogToUserService.getUsersByChatId(Integer.parseInt((String) session.getAttributes().get("chatId")));
                ServerApplication.logger.info("before for");
                for (DialogToUser d :
                        dialogsToUsers) {
                    if (d.getUser().getToken().equals(senderToken)) {
                        object.put("mess_direct", "out");
                        session.sendMessage(new TextMessage(object.toString()));
                        object.put("mess_direct", "in");
                        continue;
                    }

                    if (d.getDialog().getMessenger().getId() != 1) {
                        continue;
                    }

                    try {
                        //Пытаемся отправить сообщение отправителю в сессию
                        //Отправится при условии, что получатель сидит в диалоге
                        sessions.get(d.getUser().getToken()).sendMessage(new TextMessage(object.toString()));
                    } catch (Exception e) {

//                    String singleUseToken = generateSingleUseToken(16);
//                    logger.info("singleUseToken = " + singleUseToken);
//                    logger.info("senderId = " + senderId);
//                    MessageController.tokens.put(singleUseToken, senderId);
//
//                    Map<String, String> map = new HashMap<>();
//                    map.put("notification_token", singleUseToken);
//                    map.put("chat_id", "123");
//                    map.put("messenger_id", "3");
//
//                    PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
//
//                    //Все параметры берутся из бд
//                    pushNotificationRequest.setToken("fhsrKfmWS-61TiRkyGuqtQ:APA91bGEP1MT4p1T2nEoKLsQNn7sqLUgL2eyxHOodqeSW_uZ54Vp5YFEpHOMsMhzekLU0Rv0rb4wfj4XHcF-YsYYfslJa247koqLX335Pc5OapxFWxy1VNVe5i8HEeDsWW81l6F8i2Yo");
//                    pushNotificationRequest.setTitle(senderId);
//                    pushNotificationRequest.setMessage(content);
//                    pushNotificationRequest.setMap(map);
//
////            PushNotificationService pushNotificationService = new PushNotificationService(new FCMService());
//                    FCMService service = new FCMService();
//                    logger.info("bool = " + service);
//
//                    try {
//                        service.sendMessageToToken(pushNotificationRequest);
//                    } catch (ExecutionException executionException) {
//                        logger.error(executionException.getMessage());
//                    }
                    }
                }
                break;
            case 2:
                break;
        }
    }

    public static void sendMessageFromTelegram(TdApi.Message message, String token, String messageType) {
        ServerApplication.logger.info("void sendMessageFromTelegram New Message was gotten");
        ServerApplication.logger.info("token = " + token + " messageType = " + messageType);
        JSONObject object = new JSONObject();
        object.put("mess_rand_id", -1);
        object.put("mess_api_id", message.id);
        object.put("mess_text", ((TdApi.MessageText) message.content).text.text);
        object.put("mess_time", message.date);
        object.put("mess_direct", messageType);


        try {
            sessions.get(token).sendMessage(new TextMessage(object.toString()));
        } catch (Exception e) {
//                    String singleUseToken = generateSingleUseToken(16);
//                    logger.info("singleUseToken = " + singleUseToken);
//                    logger.info("senderId = " + senderId);
//                    MessageController.tokens.put(singleUseToken, senderId);
//
//                    Map<String, String> map = new HashMap<>();
//                    map.put("notification_token", singleUseToken);
//                    map.put("chat_id", "123");
//                    map.put("messenger_id", "3");
//
//                    PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
//
//                    //Все параметры берутся из бд
//                    pushNotificationRequest.setToken("fhsrKfmWS-61TiRkyGuqtQ:APA91bGEP1MT4p1T2nEoKLsQNn7sqLUgL2eyxHOodqeSW_uZ54Vp5YFEpHOMsMhzekLU0Rv0rb4wfj4XHcF-YsYYfslJa247koqLX335Pc5OapxFWxy1VNVe5i8HEeDsWW81l6F8i2Yo");
//                    pushNotificationRequest.setTitle(senderId);
//                    pushNotificationRequest.setMessage(content);
//                    pushNotificationRequest.setMap(map);
//
////            PushNotificationService pushNotificationService = new PushNotificationService(new FCMService());
//                    FCMService service = new FCMService();
//                    logger.info("bool = " + service);
//
//                    try {
//                        service.sendMessageToToken(pushNotificationRequest);
//                    } catch (ExecutionException executionException) {
//                        logger.error(executionException.getMessage());
//                    }
        }

    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("after established ");
        String senderToken = (String) session.getAttributes().get("senderToken");
        sessions.put(senderToken, session);
        session.sendMessage(new PongMessage());
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String senderToken = (String) session.getAttributes().get("senderToken");
        logger.info("after closed");
        sessions.remove(senderToken);
        super.afterConnectionClosed(session, status);
    }

    public String generateSingleUseToken(int length) {
        Random random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }

        return sb.toString();
    }

}
