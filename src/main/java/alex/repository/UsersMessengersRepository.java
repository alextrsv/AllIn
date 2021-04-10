package alex.repository;



import alex.entity.UsersMessengers;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UsersMessengersRepository extends CrudRepository<UsersMessengers, Integer> {



    @Query("select u from UsersMessengers u where u.user.id = :user_id AND u.messenger.id = :messenger_id")
    UsersMessengers findByUIdMId(@Param("user_id") Integer user_id, @Param("messenger_id") Integer messenger_id);


    @Transactional
    @Modifying
    @Query("DELETE FROM UsersMessengers um WHERE um.user.id = :user_id")
    void deleteByUserId(@Param("user_id") Integer user_id);

}