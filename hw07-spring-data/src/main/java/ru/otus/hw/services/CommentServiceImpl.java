package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Comment> findByBookId(long bookId) {
        List<Comment> comments = commentRepository.findByBookId(bookId);

        comments.forEach(c -> {
            c.getBook().getAuthor().getFullName();
            c.getBook().getGenres().size();
        });    // for eager loading

        return comments;
    }

    @Transactional
    @Override
    public Comment insert(long bookId, String text) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new EntityNotFoundException("Book with id " + bookId + " not found");
        }
        book.getGenres().size();    // for eager loading
        book.getAuthor().getFullName();

        return commentRepository.save(new Comment(null, book, text));
    }

    @Transactional
    @Override
    public Comment update(long id, String text) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id " + id + " not found"));
        comment.setText(text);

        comment.getBook().getGenres().size();    // for eager loading
        comment.getBook().getAuthor().getFullName();

        return commentRepository.save(comment);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}
