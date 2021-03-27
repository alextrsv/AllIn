package alex.service.impl;

import alex.dto.DialogCategoryHolder;
import alex.dto.Response;
import alex.dto.ResponseStatus;
import alex.entity.*;
import alex.repository.*;
import alex.service.CommonService;
import alex.service.DialogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class DialogServiceImpl implements DialogService {

    @Autowired
    DialogRepository dialogRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessengerRepository messengerRepository;

    @Autowired
    DialogToUserRepository dialogToUserRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    FavoritesRepository favoritesRepository;

    @Autowired
    CommonService commonService;


    @Override
    public Response createDialog(int messId, String token, User userTo) {

        User userFrom = userRepository.findByToken(token);
        userTo = userRepository.findById(userTo.getId()).get();

        List<Messenger> messengersOfUserFrom = commonService.getUsersMessengers(userFrom);
        List<Messenger> messengersOfUserTo = commonService.getUsersMessengers(userTo);

        Messenger currentMessenger = messengerRepository.findById(messId).get();

        if (!messengersOfUserFrom.contains(currentMessenger))
            return new Response(ResponseStatus.ERROR, "This user do not have such messenger");
        else if (messengersOfUserFrom.contains(currentMessenger) && !messengersOfUserTo.contains(currentMessenger))
            return new Response(ResponseStatus.ERROR, "You sobesednik do not have such messenger");
        else {
            Dialog dialog = new Dialog();
            dialog.setIcon("Icon2");
            dialog.setNote("testing http dialog adding");
            dialog.setTitle("DIALOG2");
            dialog.setMessenger(messengerRepository.findById(messId).get());

            DialogToUser dialogToUserFrom = new DialogToUser();
            dialogToUserFrom.setDialog(dialog);
            dialogToUserFrom.setUser(userFrom);

            DialogToUser dialogToUserTo = new DialogToUser();
            dialogToUserTo.setDialog(dialog);
            dialogToUserTo.setUser(userTo);


            dialogRepository.save(dialog);
            dialogToUserRepository.save(dialogToUserFrom);
            dialogToUserRepository.save(dialogToUserTo);
            return new Response(ResponseStatus.SUCCESS, "New dialog has been created");

        }
    }

    @Override
    public Dialog getById(int id) {
        return dialogRepository.findById(id).get();
    }

    @Override
    public Response addDialogsToCategories(String token, DialogCategoryHolder dialogCategoryHolder) {
        List<Dialog> dialogs = dialogCategoryHolder.getDialogs();
        List<Category> categories = dialogCategoryHolder.getCategories();


        User user = userRepository.findByToken(token);

        for (Dialog dg : dialogs) {
            Dialog dialog = dialogRepository.findById(dg.getId()).get();
            DialogToUser dialogToUser = dialogToUserRepository.findByDidUid(dialog.getId(), user.getId());
            if (dialogToUser == null)
                return new Response(ResponseStatus.ERROR, "у пользователя нет диалога c id = " + dialog.getId());
            for (Category ct : categories) {
                Category category = categoryRepository.findById(ct.getId()).get();
                if (category.getUser().getId() != user.getId())
                    return new Response(ResponseStatus.ERROR, "у пользователя нет категории c id = " + category.getId());
                Favorites favorites = new Favorites();

                favorites.setDialogToUser(dialogToUser);
                favorites.setCategory(category);

                favoritesRepository.save(favorites);
            }
        }
        return new Response(ResponseStatus.SUCCESS, "all dialogs have been added to all categories");
    }


    @Override
    public Response deleteFromCategories(String token, boolean deleteFromFavourites, DialogCategoryHolder dialogCategoryHolder) {
        List<Dialog> dialogs = dialogCategoryHolder.getDialogs();
        User user = userRepository.findByToken(token);
        List<DialogToUser> dialogsToUserToDelete = new ArrayList<DialogToUser>();
        for (Dialog dg : dialogs) {
            dialogsToUserToDelete.add(dialogToUserRepository.findByDidUid(dg.getId(), user.getId()));
        }

        if (deleteFromFavourites) {
            for (DialogToUser dialogToUserToDelete : dialogsToUserToDelete)
                favoritesRepository.deleteByDialogToUserId(dialogToUserToDelete.getId());
        } else {
            List<Category> categories = dialogCategoryHolder.getCategories();
            for (DialogToUser dialogToUserToDelete : dialogsToUserToDelete) {
                for (Category category : categories) {
                    favoritesRepository.deleteBy2Id(dialogToUserToDelete.getId(), category.getId());
                }
            }
        }

        return new Response(ResponseStatus.SUCCESS, "dialogs have been deleted from categories");
    }

    @Override
    public List<Dialog> getFavoriteDialogs(String token, Category categoryTr) {
        User user = userRepository.findByToken(token);
        Collection<DialogToUser> dialogToUserList = user.getDialogToUserCollection();
        List<Dialog> dialogs = new ArrayList<Dialog>();

        if (categoryTr.getId() != 0) {
            Category category = categoryRepository.findById(categoryTr.getId()).get();
            for (DialogToUser dialogToUser : dialogToUserList) {
                if (dialogToUser.getFavorites().size() != 0) {
                    for (Favorites fav : dialogToUser.getFavorites()) {
                        if (fav.getCategory().getId() == category.getId()) {
                            dialogs.add(dialogToUser.getDialog());
                            break;
                        }
                    }
                }
            }
        } else {
            for (DialogToUser dialogToUser : dialogToUserList) {
                if (dialogToUser.getFavorites().size() != 0) dialogs.add(dialogToUser.getDialog());
            }
        }

        return dialogs;
    }
}
