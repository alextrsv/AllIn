package alex.repository;

import alex.entity.Dialog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DialogRepository extends CrudRepository<Dialog, Integer>{

}
