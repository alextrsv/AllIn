package alex.service;


import alex.entity.Messenger;

import java.util.List;

public interface MessengerService {
    Messenger addMessenger(Messenger messenger);
    void delete(int id);
    Messenger getByName(String name);
    Messenger editMessenger(Messenger messenger);
    List<Messenger> getAll();

    Messenger getById(int messId);
}