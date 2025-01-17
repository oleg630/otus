package ru.otus.hw.repositories;

import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    List<Comment> findByBookId(long bookId);

    Optional<Comment> find(long id);

    Comment save(Comment bc);

    void deleteById(long id);

}
