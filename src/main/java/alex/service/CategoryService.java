package alex.service;

import alex.entity.Category;
import alex.exceptions.NoSuchCategoryException;
import alex.exceptions.NoSuchCategoryOwnedByUserException;
import alex.exceptions.NoSuchUserException;

public interface CategoryService {

    Category update(String token, Category category) throws NoSuchCategoryException, NoSuchUserException, NoSuchCategoryOwnedByUserException;
    Category createCategory(String token, Category category) throws NoSuchUserException;
    void delete(String token, int categoryId) throws NoSuchUserException, NoSuchCategoryException, NoSuchCategoryOwnedByUserException;
    Category getById(int id);

}
