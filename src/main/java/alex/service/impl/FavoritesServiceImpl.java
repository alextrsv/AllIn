package alex.service.impl;

import alex.entity.Favorites;
import alex.repository.FavoritesRepository;
import alex.service.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoritesServiceImpl implements FavoritesService {

    @Autowired
    FavoritesRepository favoritesRepository;

    @Override
    public Favorites getById(int id) {
        return favoritesRepository.findById(id).get();
    }

    @Override
    public Favorites edit(Favorites favorites) {
        return favoritesRepository.save(favorites);
    }

    @Override
    public List<Favorites> getAll() {
        return (List<Favorites>) favoritesRepository.findAll();
    }
}
