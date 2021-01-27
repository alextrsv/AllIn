package alex.service;

import alex.entity.Dialog;
import alex.entity.DialogToUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DialogToUserService {
    DialogToUser getById(int id);
    DialogToUser edit(DialogToUser dialogToUser);
    List<DialogToUser> getAll();
}
