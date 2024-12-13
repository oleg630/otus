package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

@Repository
@AllArgsConstructor
public class GenreRepositoryJpa implements GenreRepository {

    @PersistenceContext
    private final EntityManager em;

    @Transactional(readOnly = true)
    @Override
    public List<Genre> findAll() {
        TypedQuery<Genre> query = em.createQuery("select g from Genre g", Genre.class);
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Genre> findByIds(Set<Long> ids) {
        TypedQuery<Genre> query = em.createQuery("select g from Genre g where id in :ids", Genre.class);
        query.setParameter("ids", ids);
        return query.getResultList();
    }
}
