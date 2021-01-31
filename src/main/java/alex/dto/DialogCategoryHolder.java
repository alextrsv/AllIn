package alex.dto;

import alex.entity.Category;
import alex.entity.Dialog;

import java.util.List;

public class DialogCategoryHolder {
    private List<Category> categories;
    private List<Dialog> dialogs;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Dialog> getDialogs() {
        return dialogs;
    }

    public void setDialogs(List<Dialog> dialogs) {
        this.dialogs = dialogs;
    }
}
