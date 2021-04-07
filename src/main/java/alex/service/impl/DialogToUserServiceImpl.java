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
    public List<DialogToUser> getAll() {
        return (List<DialogToUser>) dialogToUserRepository.findAll();
    }

    @Override
    public List<DialogToUser> getUsersByChatId(int dialog_id) {
        return (List<DialogToUser>)dialogToUserRepository.findDialogToUsersByDialog_Id(dialog_id);
    }

    @Override
    public void saveDialogToUser(DialogToUser dialogToUser) {
        dialogToUserRepository.save(dialogToUser);
    }


}
