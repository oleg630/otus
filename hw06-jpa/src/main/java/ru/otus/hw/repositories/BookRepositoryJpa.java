package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class BookRepositoryJpa implements BookRepository {

    @PersistenceContext
    private final EntityManager em;

    @Transactional(readOnly = true)
    @Override
    public Optional<Book> findById(long id) {
        return Optional.ofNullable(em.find(Book.class, id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> findAll() {
        TypedQuery<Book> query = em.createQuery("select b from Book b join fetch b.author", Book.class);
        return query.getResultList();
    }

    @Transactional
    @Override
    public Book save(Book book) {
        return em.merge(book);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        findById(id).ifPresent(em::remove);
    }

    @Transactional
    @Override
    public BookComment addComment(BookComment bc) {
        return em.merge(bc);
    }
}
