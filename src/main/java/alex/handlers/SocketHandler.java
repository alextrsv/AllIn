package alex.handlers;

import alex.ServerApplication;
import alex.controllers.MessageController;
import alex.controllers.TelegramController;
import alex.fcm_base.FCMService;
import alex.model.PushNotificationRequest;
import it.tdlight.jni.TdApi;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Component
public class SocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);
    private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890@$#^&?*()}{][%";
    public static Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        logger.info("recieve Pong Message");
//        handleMessage(session, message);
        super.handlePongMessage(session, message);
    }


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException, IOException {
        JSONObject object1 = new JSONObject(message.getPayload());
        ServerApplication.logger.info("handleTextMessage method");
        long messageId = object1.getLong("mess_rand_id");
        String content = object1.getString("mess_text");
        int time = object1.getInt("mess_time");

        ServerApplication.logger.info("mess_rand_id = " + messageId + " content = " + content);
//        String senderToken = (String) session.getAttributes().get("senderToken");


        switch ((int) session.getAttributes().get("messengerId")) {
            case 1:
                ServerApplication.logger.info("token1 = " + session.getAttributes().get("senderToken"));

                TelegramController.clients.get((String) session.getAttributes().get("senderToken")).setMessRandId(messageId);
                TdApi.Message message1 = (TelegramController.clients.get((String) session.getAttributes().get("senderToken"))).sendMessage((String) session.getAttributes().get("chatId"), content);

                JSONObject object = new JSONObject();

                object.put("mess_rand_id", messageId);
                object.put("mess_api_id", message1.id);
                object.put("mess_text", content);
                object.put("mess_direct", "out");
                object.put("mess_time", time);

                //Отправка сообщения в сессию отпправителю
                //mess_rand_id != -1 AND type = out
                try {
                    session.sendMessage(new TextMessage(object.toString()));
                } catch (Exception e) {
                    logger.info("Can't send message to sender");
                }

                break;
            case 2:
                break;
        }
    }

    public static void sendMessageFromTelegram(TdApi.Message message, String token, String messageType, String msgToken) {
        ServerApplication.logger.info("void sendMessageFromTelegram New Message was gotten");
        ServerApplication.logger.info("token = " + token + " messageType = " + messageType);
        JSONObject object = new JSONObject();
//        object.put("mess_rand_id", messRandId);
        object.put("mess_api_id", message.id);
        object.put("mess_text", ((TdApi.MessageText) message.content).text.text);
        object.put("mess_time", message.date);
        object.put("mess_direct", messageType);

        try {
            //Пытаемся отправить сообщение отправителю в сессию
            //Отправится при условии, что получатель сидит в диалоге
            ServerApplication.logger.info("before sessions.get");
            sessions.get(token).sendMessage(new TextMessage(object.toString()));

        } catch (Exception e) {
            ServerApplication.logger.info("push notification");

            String singleUseToken = generateSingleUseToken(16);
            logger.info("singleUseToken = " + singleUseToken);
            logger.info("senderId = " + token);
            MessageController.tokens.put(singleUseToken, token);

            Map<String, String> map = new HashMap<>();
            map.put("notification_token", singleUseToken);
            map.put("chat_id", message.id + "");
            map.put("messenger_id", "1");

            PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();

            pushNotificationRequest.setToken(msgToken);
            pushNotificationRequest.setTitle(token);
            pushNotificationRequest.setMessage(((TdApi.MessageText) message.content).text.text);
            pushNotificationRequest.setMap(map);

            FCMService service = new FCMService();
            logger.info("bool = " + service);

            try {
                service.sendMessageToToken(pushNotificationRequest);
            } catch (ExecutionException | InterruptedException executionException) {
                logger.info("Execptioooooon");
                logger.error(executionException.getMessage());
            }
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

    public static String generateSingleUseToken(int length) {
        Random random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }

        return sb.toString();
    }

}
