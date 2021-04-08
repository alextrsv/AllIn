package alex.service;

import alex.entity.DialogToUser;

import java.util.List;


public interface DialogToUserService {
    DialogToUser getById(int id);
    List<DialogToUser> getAll();
    List<DialogToUser> getUsersByChatId(int dialog_id);
    void saveDialogToUser(DialogToUser dialogToUser);
}