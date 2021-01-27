package alex.service;

import alex.entity.Category;
import alex.entity.User;

import java.util.Optional;

public interface CategoryService {

    void delete(int id);
    Category getById(int id);
    Category editCategory(Category category);
    Category findByTitle(String title);

}
