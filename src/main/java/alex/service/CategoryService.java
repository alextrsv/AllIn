package alex.service;

import alex.dto.Response;
import alex.entity.Category;
import alex.entity.User;

import java.util.Optional;

public interface CategoryService {

    Category update(String token, Category category);
    Category createCategory(String token, Category category);
    Response delete(String token, int categoryId);
    Category getById(int id);

}
