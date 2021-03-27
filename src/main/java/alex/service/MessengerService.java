package alex.service;


import alex.dto.MessengerDto;
import alex.dto.Response;
import alex.entity.Messenger;

import java.util.List;

public interface MessengerService {


    Response changePosition(String token, MessengerDto messenger);
    void removeMessenger(String token, List<Messenger> messengersToDelete);
    void delete(int id);
    Iterable<Messenger> getAll();
    Messenger getById(int messId);
}