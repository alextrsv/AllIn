package alex.service.impl;

import alex.entity.Dialog;
import alex.repository.DialogRepository;
import alex.service.DialogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DialogServiceImpl implements DialogService {

    @Autowired
    DialogRepository dialogRepository;

    @Override
    public Dialog getById(int id) {
        return dialogRepository.findById(id).get();
    }

    @Override
    public Dialog edit(Dialog dialog) {
        return dialogRepository.save(dialog);
    }

    @Override
    public List<Dialog> getAll() {
        return (List<Dialog>) dialogRepository.findAll();
    }
}
