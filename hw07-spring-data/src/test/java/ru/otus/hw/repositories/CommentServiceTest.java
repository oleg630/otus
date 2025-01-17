package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.BookServiceImpl;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.CommentServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@DisplayName("Сервис для работы с комментами")
@DataJpaTest
@DirtiesContext
@Transactional(propagation = Propagation.NEVER)
@Import({CommentServiceImpl.class, BookServiceImpl.class})
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BookService bookService;

    private final Comment testComment;

    CommentServiceTest() {
        Book book = new Book(1L, "BookTitle_1", new Author(1L, "Author_1"),
                List.of(new Genre(1L, "Genre_1"), new Genre(2L, "Genre_2")));
        testComment = new Comment(1L, book, "my comment 1");
    }


    @DisplayName("должен загружать информацию о комменте по  id")
    @Test
    void shouldFindExpectedCommentByBookId() {
        List<Comment> actualComments = commentService.findByBookId(1L);
        assertEquals(1, actualComments.size());
        compareTwoComments(testComment, actualComments.get(0));
    }

    @DisplayName("должен сохранять новый коммент и удалять")
    @Test
    void shouldSaveNewComment() {
        var book = bookService.findById(2L);
        var expectedComment = new Comment(null, book.get(), "CommentTitle_10500");
        var returnedComment = commentService.insert(expectedComment.getBook().getId(), expectedComment.getText());
        assertThat(returnedComment).isNotNull().matches(Comment -> Comment.getId() > 0);
        compareTwoComments(expectedComment, returnedComment);

        compareTwoComments(returnedComment, commentService.findByBookId(returnedComment.getBook().getId()).get(0));

        commentService.deleteById(returnedComment.getId());
        assertThat(commentService.findByBookId(2L)).isEmpty();
    }

    @DisplayName("должен сохранять измененный коммент")
    @Test
    void shouldSaveUpdatedComment() {
        var book = bookService.findById(1L).get();
        var expectedComment = new Comment(1L, book, "CommentTitle_10500");

        assertNotEquals(expectedComment.getText(),
                commentService.findByBookId(expectedComment.getId()).get(0).getText());

        var returnedComment = commentService.update(expectedComment.getId(), expectedComment.getText());
        compareTwoComments(expectedComment, returnedComment);

        compareTwoComments(returnedComment, commentService.findByBookId(returnedComment.getId()).get(0));

        // откат
        commentService.update(testComment.getId(), testComment.getText());
    }

    private void compareTwoComments(Comment expected, Comment actual) {
        assertThat(actual).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expected);
    }
}