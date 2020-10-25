package alex.service;


import alex.entity.User;

import java.util.List;


public interface UserService {

    User addUser(User user);
    void delete(int id);
    User getByName(String name);
    User getById(int id);
    User editUser(User user);
    List<User> getAll();
//    void update(User user);

}