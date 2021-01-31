package alex.repository;

import alex.entity.Favorites;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FavoritesRepository extends CrudRepository<Favorites, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Favorites WHERE dialogToUser.id = :dialog_to_user_id")
    void deleteByDialogToUserId(@Param("dialog_to_user_id") Integer dialogToUserId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Favorites WHERE dialogToUser.id = :dialog_to_user_id AND category.id = :category_id")
    void deleteBy2Id(@Param("dialog_to_user_id") Integer dialogToUserId, @Param("category_id") Integer categoryId);
}
