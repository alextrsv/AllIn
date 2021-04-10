package alex.service.impl;

import alex.dto.Response;
import alex.entity.*;
import alex.exceptions.NoSuchCategoryException;
import alex.exceptions.NoSuchCategoryOwnedByUserException;
import alex.exceptions.NoSuchUserException;
import alex.repository.CategoryRepository;
import alex.repository.UserRepository;
import alex.service.CategoryService;
import alex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;


    @Override
    public Category update(String token, Category category) throws NoSuchCategoryException, NoSuchUserException, NoSuchCategoryOwnedByUserException {
        Category updCategory;
        try {
            updCategory = categoryRepository.findById(category.getId()).get();
        }catch (NoSuchElementException ex) {throw new NoSuchCategoryException(category.getId());}

        User user = userService.getByToken(token);
        if (!user.getCategories().contains(updCategory)) throw new NoSuchCategoryOwnedByUserException(category.getId(), token);
        else {
            updCategory.setTitle(category.getTitle());
            categoryRepository.save(updCategory);
        }

        return  updCategory;
    }

    @Override
    public Category createCategory(String token, Category category) throws NoSuchUserException {
        User user = userService.getByToken(token);
        category.setUser(user);
        user.getCategories().add(category);
        userRepository.save(user);

        return categoryRepository.findByName(category.getTitle());
    }

    @Override
    public void delete(String token, int categoryId) throws NoSuchUserException,
            NoSuchCategoryException, NoSuchCategoryOwnedByUserException {

        User user = userService.getByToken(token);
        Category category;
        try {
            category = categoryRepository.findById(categoryId).get();
        }catch (NoSuchElementException noSuchElementException){
            throw new NoSuchCategoryException(categoryId);
        }

        if(!user.getCategories().contains(category)) throw new NoSuchCategoryOwnedByUserException(categoryId, token);
        else {
            user.getCategories().remove(category);
            categoryRepository.deleteById(categoryId);
            userRepository.save(user);
        }
    }

    @Override
    public Category getById(int id) {
        return categoryRepository.findById(id).get();
    }

}
