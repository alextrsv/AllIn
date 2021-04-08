package alex.service.impl;

import alex.dto.MessengerDto;
import alex.dto.Response;
import alex.entity.Messenger;
import alex.entity.User;
import alex.entity.UsersMessengers;
import alex.exceptions.MessengerAlreadyOwnedException;
import alex.exceptions.NoSuchUserException;
import alex.repository.MessengerRepository;
import alex.repository.UserRepository;
import alex.repository.UsersMessengersRepository;
import alex.service.CommonService;
import alex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.NoSuchElementException;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsersMessengersRepository usersMessengersRepository;

    @Autowired
    private MessengerRepository messengerRepository;

    @Autowired
    private CommonService commonService;


    @Override
    public void setMsgToken(String token, String msgToken) throws NoSuchUserException {
        User user = getByToken(token);
        user.setMsgToken(msgToken);
        userRepository.save(user);
    }

    @Override
    public User addUser(String token, User newUser) {
        if (userRepository.findByToken(token) == null) {   //пользователя нет, создается новый
            newUser.setToken(token);
            return userRepository.save(newUser);
        }
        else return userRepository.findByToken(token); // уже есть - возвращается профиль
    }

    @Override
    public Iterable<Messenger> getUsersMess(String token) throws NoSuchUserException {
        User user = getByToken(token);
        Collection<Messenger> usrMessengers = commonService.getUsersMessengers(user);
        Iterable<Messenger> allMessengers = messengerRepository.findAll();
        for (Messenger mess : allMessengers) {
            mess.setActivated(usrMessengers.contains(mess));
        }
        return allMessengers;
    }

    @Override
    public void addMessenger(String token, String accessToken, MessengerDto messengerDto)
            throws NoSuchElementException, MessengerAlreadyOwnedException, NoSuchUserException {

        User user = getByToken(token);

        Messenger messenger = messengerRepository.findById(messengerDto.getId()).get();

        if (commonService.getUsersMessengers(user).contains(messenger))
            throw new MessengerAlreadyOwnedException(messenger.getTitle(), token);

        UsersMessengers newUsersMessengers = new UsersMessengers();

        newUsersMessengers.setUser(user);
        newUsersMessengers.setMessenger(messenger);
        newUsersMessengers.setAccessToken(accessToken);
        newUsersMessengers.setPosition(user.getUsMes().size()+1);

        usersMessengersRepository.save(newUsersMessengers);
    }


    @Override
    public void delete(String token) throws NullPointerException, NoSuchUserException {
        usersMessengersRepository.deleteByUserId(getByToken(token).getId());
        userRepository.deleteByToken(token);
    }

    @Override
    public User getByToken(String token) throws NoSuchUserException {
        User user = userRepository.findByToken(token);
        if (user == null) throw new NoSuchUserException(token);
        return user;
    }

    @Override
    public User getById(int id) {
        return userRepository.findById(id).get();
    }

    @Override
    public Iterable<User> getAll() {
        return userRepository.findAll();
    }

}