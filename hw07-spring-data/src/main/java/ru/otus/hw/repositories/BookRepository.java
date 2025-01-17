package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends CrudRepository<Book, Long> {
    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "book_author_graph")
    Optional<Book> findById(long id);

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "book_author_graph")
    List<Book> findAll();

    @EntityGraph(type = EntityGraph.EntityGraphType.FETCH, value = "book_author_graph")
    Book save(Book book);

    void deleteById(long id);

}
