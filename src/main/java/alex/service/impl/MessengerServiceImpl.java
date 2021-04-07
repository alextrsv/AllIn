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
import alex.service.MessengerService;
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


    @Override
    public Response changePosition(String token, MessengerDto messenger) {
        UsersMessengers usersMessengers =
                usersMessengersRepository.findByUIdMId(userRepository.findByToken(token).getId(), messenger.getId());

        usersMessengers.setPosition(messenger.getPosition());
        usersMessengersRepository.save(usersMessengers);

        return new Response(ResponseStatus.SUCCESS, "position has been changed");
    }

    @Override
    public void removeMessenger(String token, List<Messenger> messengersToDelete) {
        User user = userRepository.findByToken(token);
        for (Messenger mess: messengersToDelete) {
            UsersMessengers usersMessengers =  usersMessengersRepository.findByUIdMId(user.getId(), mess.getId());
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