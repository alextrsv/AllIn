package alex.service;

import alex.entity.Favorites;

import java.util.List;


public interface FavoritesService {
    Favorites getById(int id);
    List<Favorites> getAll();
    void deleteByDialogToUserId(int dialogToUserId);
}