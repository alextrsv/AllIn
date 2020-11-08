package alex.repository;



import alex.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    @Query("select u from User u where u.firstName = :name")
    User findByName(@Param("name") String name);

    @Transactional
    @Modifying
    @Query("DELETE FROM User WHERE token = :token")
    void deleteByToken(@Param("token") String token);

//    @Query("update User set  where u.firstName = :name")
//    void update(User user);
}