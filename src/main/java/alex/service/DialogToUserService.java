package alex.service;

import alex.entity.DialogToUser;
import java.util.List;


public interface DialogToUserService {
    DialogToUser getById(int id);
    DialogToUser edit(DialogToUser dialogToUser);
    List<DialogToUser> getAll();
}
