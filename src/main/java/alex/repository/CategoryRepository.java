package alex.repository;

import alex.entity.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {


    @Query("select c from Category c where c.title = :name")
    Category findByName(@Param("name") String name);
}
