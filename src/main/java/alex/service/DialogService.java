package alex.service;

import alex.entity.Dialog;
import alex.entity.User;

import java.util.List;

public interface DialogService {
    Dialog getById(int id);
    Dialog edit(Dialog dialog);
    List<Dialog> getAll();
}
