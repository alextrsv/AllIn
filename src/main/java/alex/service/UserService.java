package alex.service;


import alex.dto.MessengerDto;
import alex.dto.Response;
import alex.entity.Messenger;
import alex.entity.User;


public interface UserService {


    Response setMsgToken(String token, String msgToken);
    User addUser(String token, User newUser);
    Iterable<Messenger> getUsersMess(String token);
    void addMessenger(String token, String accessToken, MessengerDto messengerDto);

    String delete(String token);
    User getByToken(String token);
    User getById(int id);
    Iterable<User> getAll();

}