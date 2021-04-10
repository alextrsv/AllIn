package alex.service;


import alex.dto.MessengerDto;
import alex.dto.Response;
import alex.entity.Messenger;
import alex.entity.User;
import alex.exceptions.MessengerAlreadyOwnedException;
import alex.exceptions.NoSuchUserException;


public interface UserService {


    void setMsgToken(String token, String msgToken) throws NoSuchUserException;
    User addUser(String token, User newUser);
    Iterable<Messenger> getUsersMess(String token) throws NoSuchUserException;
    void addMessenger(String token, String accessToken, MessengerDto messengerDto) throws  MessengerAlreadyOwnedException, NoSuchUserException;

    void delete(String token) throws NoSuchUserException;
    User getByToken(String token) throws NoSuchUserException;
    User getById(int id);
    Iterable<User> getAll();

}