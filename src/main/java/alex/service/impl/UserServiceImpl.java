package alex.service.impl;

import alex.dto.MessengerDto;
import alex.dto.Response;
import alex.dto.ResponseStatus;
import alex.entity.Messenger;
import alex.entity.User;
import alex.entity.UsersMessengers;
import alex.repository.MessengerRepository;
import alex.repository.UserRepository;
import alex.repository.UsersMessengersRepository;
import alex.service.CommonService;
import alex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;


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
    public Response setMsgToken(String token, String msgToken) {
        try {
            User user = userRepository.findByToken(token);
            user.setMsgToken(msgToken);
            userRepository.save(user);
        }catch (NullPointerException exeption){
            return new Response(ResponseStatus.ERROR, "there isn't such user. Check auth token");
        }
        return new Response(ResponseStatus.SUCCESS, "msgToken has been set successfully set up");
    }

    @Override
    public User addUser(String token, User newUser) {
        if (userRepository.findByToken(token) == null) {   //пользователя нет, создается новый
            newUser.setToken(token);
            return userRepository.save(newUser);
        }
        else return userRepository.findByToken(token);
    }

    @Override
    public Iterable<Messenger> getUsersMess(String token) {
        User user = userRepository.findByToken(token);
        Collection<Messenger> usrMessengers = commonService.getUsersMessengers(user);
        Iterable<Messenger> allMessengers = messengerRepository.findAll();
        for (Messenger mess : allMessengers) {
            mess.setActivated(usrMessengers.contains(mess));
        }
        return allMessengers;
    }

    @Override
    public void addMessenger(String token, String accessToken, MessengerDto messengerDto) {
        User user = userRepository.findByToken(token);
        Messenger messenger = messengerRepository.findById(messengerDto.getId()).get();
        UsersMessengers newUsersMessengers = new UsersMessengers();

        newUsersMessengers.setUser(user);
        newUsersMessengers.setMessenger(messenger);
        newUsersMessengers.setAccessToken(accessToken);
        newUsersMessengers.setPosition(user.getUsMes().size()+1);

        usersMessengersRepository.save(newUsersMessengers);
    }


    @Override
    public String delete(String token) {
        usersMessengersRepository.deleteByUserId(getByToken(token).getId());
        userRepository.deleteByToken(token);
        return "redirect:/users";
    }

    @Override
    public User getByToken(String token) {
        return userRepository.findByToken(token);
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