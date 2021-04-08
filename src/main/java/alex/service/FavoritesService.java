package alex.service;

import alex.entity.Dialog;
import alex.entity.Favorites;
import org.springframework.stereotype.Service;

import java.util.List;


public interface FavoritesService {
    Favorites getById(int id);
    List<Favorites> getAll();
    void deleteByDialogToUserId(int dialogToUserId);
}
