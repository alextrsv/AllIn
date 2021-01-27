package alex.service.impl;

import alex.entity.*;
import alex.repository.CategoryRepository;
import alex.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;


    @Override
    public void delete(int id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public Category getById(int id) {
        return categoryRepository.findById(id).get();
    }

    @Override
    public Category editCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category findByTitle(String title) {
        return categoryRepository.findByName(title);
    }
}
