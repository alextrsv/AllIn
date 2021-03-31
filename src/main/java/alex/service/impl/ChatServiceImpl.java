//package alex.service.impl;
//
//import alex.dto.MessengerDto;
//import alex.entity.Chat;
//import alex.repository.ChatRepository;
//import alex.service.ChatService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class ChatServiceImpl implements ChatService {
//
//    @Autowired
//    ChatRepository chatRepository;
//
//    @Override
//    public List<Chat> getChats(long chatId) {
//        return chatRepository.findByChatId(chatId);
//    }
//
//    @Override
//    public void addChat(long id, String token, int messenger_id) {
//
//    }
//}
