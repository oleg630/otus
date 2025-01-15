package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    @EntityGraph(attributePaths = {"book.author"})
    List<Comment> findByBookId(long bookId);

    @EntityGraph(attributePaths = {"book.author"})
    Optional<Comment> findById(long id);

    @EntityGraph(attributePaths = {"book.author"})
    Comment save(Comment bc);

    void deleteById(long id);

}
