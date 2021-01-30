package alex.service.impl;

import alex.entity.DialogToUser;
import alex.repository.DialogToUserRepository;
import alex.service.DialogToUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DialogToUserServiceImpl implements DialogToUserService {
    @Autowired
    DialogToUserRepository dialogToUserRepository;

    @Override
    public DialogToUser getById(int id) {
        return dialogToUserRepository.findById(id).get();
    }

    @Override
    public DialogToUser edit(DialogToUser dialogToUser) {
        return dialogToUserRepository.save(dialogToUser);
    }

    @Override
    public List<DialogToUser> getAll() {
        return (List<DialogToUser>) dialogToUserRepository.findAll();
    }
}
