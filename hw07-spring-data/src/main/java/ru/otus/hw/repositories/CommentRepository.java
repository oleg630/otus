package ru.otus.hw.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findByBookId(long bookId);

    Optional<Comment> findById(long id);

    Comment save(Comment bc);

    void deleteById(long id);

}
