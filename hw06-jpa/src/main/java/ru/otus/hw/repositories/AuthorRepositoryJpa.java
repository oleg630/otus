package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class AuthorRepositoryJpa implements AuthorRepository {

    @PersistenceContext
    private final EntityManager em;

    @Transactional(readOnly = true)
    @Override
    public List<Author> findAll() {
        TypedQuery<Author> query = em.createQuery("select a from Author a", Author.class);
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Author> findById(long id) {
        return Optional.ofNullable(em.find(Author.class, id));
    }
}
