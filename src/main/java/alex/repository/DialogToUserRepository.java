package alex.repository;

import alex.entity.Dialog;
import alex.entity.DialogToUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DialogToUserRepository extends CrudRepository<DialogToUser, Integer>  {
}
