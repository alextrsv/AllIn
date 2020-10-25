package alex.service.impl;

import alex.entity.Messenger;
import alex.repository.MessengerRepository;
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


    @Override
    public Messenger addMessenger(Messenger Messenger) {
        Messenger savedMessenger = messengerRepository.save(Messenger);
        return savedMessenger;
    }

    @Override
    public void delete(int id) {
        messengerRepository.deleteById(id);
    }

    @Override
    public Messenger getByName(String name) {
        return messengerRepository.findByName(name);
    }

    @Override
    public Messenger getById(int messId) {
        return messengerRepository.findById(messId).get();
    }

    @Override
    public Messenger editMessenger(Messenger Messenger) {
        return messengerRepository.save(Messenger);
    }

    @Override
    public List<Messenger> getAll() {
        return (List<Messenger>) messengerRepository.findAll();
    }
}