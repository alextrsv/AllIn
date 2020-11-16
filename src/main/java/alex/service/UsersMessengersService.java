package alex.service;

import alex.entity.UsersMessengers;

import java.util.List;

public interface UsersMessengersService {

    UsersMessengers addUsersMessengers(UsersMessengers usersMessengers);
    void delete(int id);
//    List<UsersMessengers> getByUsersId(Integer id);
//    UsersMessengers getById(int id);
    UsersMessengers editUsersMessengers(UsersMessengers usersMessengers);
    List<UsersMessengers> getAll();
    UsersMessengers getByUIdMId(int id, int messid);
    void deleteByUserId(int user_id);
}
