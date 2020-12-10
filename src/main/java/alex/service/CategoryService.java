package alex.service;

import alex.entity.Category;

import java.util.Optional;

public interface CategoryService {

    void delete(int id);
    Category getById(int id);

}
