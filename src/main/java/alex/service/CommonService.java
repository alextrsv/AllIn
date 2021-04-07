package alex.service;

import alex.entity.Messenger;
import alex.entity.User;
import alex.entity.UsersMessengers;
import alex.repository.MessengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommonService {

    @Autowired
    MessengerRepository messengerRepository;

    public  List<Messenger> getUsersMessengers(User user){

        List<Messenger> messengersOfUser = new ArrayList<Messenger>();

        for (UsersMessengers useMes: user.getUsMes()) {
            messengersOfUser.add(useMes.getMessenger());
        }
        Iterable<Messenger> allMessengers = messengerRepository.findAll();
        for (Messenger mess : allMessengers) {
            mess.setActivated(messengersOfUser.contains(mess));
        }
        return  messengersOfUser;
    }
}
