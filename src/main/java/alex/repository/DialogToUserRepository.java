package alex.repository;

import alex.entity.Dialog;
import alex.entity.DialogToUser;
import alex.entity.UsersMessengers;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DialogToUserRepository extends CrudRepository<DialogToUser, Integer>  {

    @Query("select du from DialogToUser du where du.user.id = :user_id AND du.dialog.id = :dialog_id AND" +
            " du.user.id = :user_id")
    DialogToUser findByDidUid(@Param("dialog_id") Integer dialogId, @Param("user_id") Integer userId);

    @Query("select du from DialogToUser du where du.dialog.id = :dialog_id")
    Iterable<DialogToUser> findByDid(@Param("dialog_id") Integer dialogId);



}
