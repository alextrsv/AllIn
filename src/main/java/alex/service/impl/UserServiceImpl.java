package alex.service.impl;

import alex.entity.User;
import alex.repository.UserRepository;
import alex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public User addUser(User User) {
        User savedUser = userRepository.save(User);
        return savedUser;
    }

    @Override
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public void delete(String token) {
        userRepository.deleteByToken(token);
    }

    @Override
    public User getByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public User getByToken(String token) {
        return userRepository.findByToken(token);
    }

    @Override
    public User getById(int id) {
        return userRepository.findById(id).get();
    }

    @Override
    public User editUser(User User) {
        return userRepository.save(User);
    }

    @Override
    public List<User> getAll() {
        return (List<User>) userRepository.findAll();
    }



//    @Override
//    public void update(User user) {
//        userRepository.update(user);
//    }
}