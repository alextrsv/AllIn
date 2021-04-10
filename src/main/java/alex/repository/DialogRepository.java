package alex.repository;

import alex.entity.Category;
import alex.entity.Dialog;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface DialogRepository extends CrudRepository<Dialog, Integer>{

    @Query("select d from Dialog d where d.apiDialogId = :apiDialogId AND d.messenger.id = :messengerId")
    Dialog findBy2Ids(@Param("apiDialogId") long apiDialogId, @Param("messengerId") int messengerId);
}
