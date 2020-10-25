package alex.repository;


import alex.entity.Messenger;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessengerRepository extends CrudRepository<Messenger, Integer> {

    @Query("select m from Messenger m where m.title = :name")
    Messenger findByName(@Param("name") String name);


}