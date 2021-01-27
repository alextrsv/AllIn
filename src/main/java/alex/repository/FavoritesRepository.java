package alex.repository;

import alex.entity.Favorites;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoritesRepository extends CrudRepository<Favorites, Integer> {
}
