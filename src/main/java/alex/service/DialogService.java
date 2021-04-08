package alex.service;

import alex.dto.DialogCategoryHolder;
import alex.dto.Response;
import alex.entity.Category;
import alex.entity.Dialog;
import alex.entity.User;

import java.util.List;

public interface DialogService {


    List<Dialog> getFavoriteDialogs(String token, Category categoryTr);
    Response deleteFromCategories(String token, boolean deleteFromFavorites, DialogCategoryHolder dialogCategoryHolder);
    Response addDialogsToCategories(String token, DialogCategoryHolder dialogCategoryHolder);
    Response createDialog(int messId, String token, User userTo);

    Dialog getById(int id);

}