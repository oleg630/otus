package ru.otus.hw.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

public interface GenreRepository extends CrudRepository<Genre, Long> {
    List<Genre> findAll();

    List<Genre> findByIdIn(Set<Long> id);
}
