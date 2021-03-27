package alex.service;

import alex.entity.DialogToUser;
import java.util.List;


public interface DialogToUserService {
    DialogToUser getById(int id);
    List<DialogToUser> getAll();
}
