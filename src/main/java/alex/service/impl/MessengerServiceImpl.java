package alex.service.impl;

import alex.dto.MessengerDto;
import alex.entity.Messenger;
import alex.entity.User;
import alex.entity.UsersMessengers;
import alex.exceptions.NoSuchMesengerOwnedException;
import alex.exceptions.NoSuchUserException;
import alex.repository.MessengerRepository;
import alex.repository.UserRepository;
import alex.repository.UsersMessengersRepository;
import alex.service.MessengerService;
import alex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class MessengerServiceImpl implements MessengerService {

    @Autowired
    private MessengerRepository messengerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UsersMessengersRepository usersMessengersRepository;

    @Autowired
    UserService userService;


    @Override
    public void changePosition(String token, MessengerDto messenger) throws NoSuchUserException, NoSuchMesengerOwnedException {
        if (userRepository.findByToken(token) == null) throw new NoSuchUserException(token);

        UsersMessengers usersMessengers =
                usersMessengersRepository.findByUIdMId(userRepository.findByToken(token).getId(), messenger.getId());

        if(usersMessengers == null) throw new NoSuchMesengerOwnedException(messenger.getId(), token);

        usersMessengers.setPosition(messenger.getPosition());
        usersMessengersRepository.save(usersMessengers);

    }

    @Override
    public void removeMessenger(String token, List<Messenger> messengersToDelete)
            throws NoSuchUserException, NullPointerException, NoSuchMesengerOwnedException {

        User user = userService.getByToken(token);

        for (Messenger mess: messengersToDelete) {
            UsersMessengers usersMessengers = usersMessengersRepository.findByUIdMId(user.getId(), mess.getId());
            if (usersMessengers == null) throw new NoSuchMesengerOwnedException(mess.getId(), token);
            usersMessengersRepository.deleteById(usersMessengers.getId());
        }
    }

    @Override
    public void delete(int id) {
        messengerRepository.deleteById(id);
    }

    @Override
    public Messenger getById(int messId) {
        return messengerRepository.findById(messId).get();
    }

    @Override
    public List<Messenger> getAll() {
        return (List<Messenger>) messengerRepository.findAll();
    }
}