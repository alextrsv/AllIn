package alex.service.impl;

import alex.dto.Response;
import alex.dto.ResponseStatus;
import alex.entity.Category;
import alex.entity.User;
import alex.repository.CategoryRepository;
import alex.repository.UserRepository;
import alex.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;


    @Override
    public Category update(String token, Category category) {
        Category updCategory = categoryRepository.findById(category.getId()).get();
        updCategory.setTitle(category.getTitle());
        categoryRepository.save(updCategory);

        return  updCategory;
    }

    @Override
    public Category createCategory(String token, Category category) {
        User user = userRepository.findByToken(token);
        category.setUser(user);
        user.getCategories().add(category);
        userRepository.save(user);

        return categoryRepository.findByName(category.getTitle());
    }

    @Override
    public Response delete(String token, int categoryId) {
        User user = userRepository.findByToken(token);
        Category category = categoryRepository.findById(categoryId).get();
        if(user.getCategories().contains(category)){
            user.getCategories().remove(category);
            categoryRepository.deleteById(categoryId);
            userRepository.save(user);
            return new Response(ResponseStatus.SUCCESS, "category has been deleted");
        }
        else return new Response(ResponseStatus.ERROR, "current user doesn't have such category");
    }

    @Override
    public Category getById(int id) {
        return categoryRepository.findById(id).get();
    }

}
