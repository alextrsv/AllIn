package alex.service;


import alex.dto.MessengerDto;
import alex.entity.Messenger;
import alex.exceptions.NoSuchMesengerOwnedException;
import alex.exceptions.NoSuchUserException;

import java.util.List;

public interface MessengerService {


    void changePosition(String token, MessengerDto messenger) throws NoSuchUserException, NoSuchMesengerOwnedException;
    void removeMessenger(String token, List<Messenger> messengersToDelete) throws NoSuchUserException, NullPointerException, NoSuchMesengerOwnedException;
    void delete(int id);
    Iterable<Messenger> getAll();
    Messenger getById(int messId);
}