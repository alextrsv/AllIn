package alex.service;

import alex.entity.Dialog;
import alex.entity.Favorites;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FavoritesService {
    Favorites getById(int id);
    Favorites edit(Favorites favorites);
    List<Favorites> getAll();
}
