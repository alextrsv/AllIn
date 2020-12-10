package alex.service.impl;

import alex.entity.UsersMessengers;
import alex.repository.UsersMessengersRepository;
import alex.service.UsersMessengersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class UsersMessengersImpl implements UsersMessengersService {

    @Autowired
    private UsersMessengersRepository usersMessengersRepository;

    @Override
    public UsersMessengers addUsersMessengers(UsersMessengers usersMessengers) {
        usersMessengersRepository.save(usersMessengers);
        return usersMessengers;
    }

    @Override
    public void delete(int id) {
        usersMessengersRepository.deleteById(id);
    }

//    @Override
//    public List<UsersMessengers> getByUsersId(Integer id) {
//        return usersMessengersRepository.findByUsersId(id);
//    }

    @Override
    public UsersMessengers editUsersMessengers(UsersMessengers usersMessengers) {
        return usersMessengersRepository.save(usersMessengers);
    }

    @Override
    public List<UsersMessengers> getAll() {
        return (List<UsersMessengers>) usersMessengersRepository.findAll();
    }

    @Override
    public UsersMessengers getByUIdMId(int id, int messid) {
        return usersMessengersRepository.findByUIdMId(id, messid);
    }

    @Override
    public void deleteByUserId(int user_id) {
        usersMessengersRepository.deleteByUserId(user_id);
    }

}
