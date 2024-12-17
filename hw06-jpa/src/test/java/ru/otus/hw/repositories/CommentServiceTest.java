package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.CommentServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с комментами")
@DataJpaTest
@DirtiesContext
@Import({JpaCommentRepository.class, CommentServiceImpl.class})
class CommentServiceTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentService commentService;

    @DisplayName("должен загружать информацию о комменте по  id")
    @Test
    void shouldFindExpectedCommentByBookId() {
        List<Comment> actualComments = commentService.findByBookId(1);
        Comment expectedComment = em.find(Comment.class, 1);
        assertThat(actualComments).singleElement().isEqualToComparingFieldByField(expectedComment);
    }

    @DisplayName("должен сохранять новый коммент")
    @Test
    void shouldSaveNewComment() {
        var expectedComment = new Comment(null, 2L, "CommentTitle_10500");
        var returnedComment = commentService.insert(expectedComment.getBookId(), expectedComment.getText());
        assertThat(returnedComment).isNotNull()
                .matches(Comment -> Comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        assertThat(commentService.findByBookId(returnedComment.getBookId()))
                .singleElement()
                .isEqualTo(returnedComment);
    }

    @DisplayName("должен сохранять измененный коммент")
    @Test
    void shouldSaveUpdatedComment() {
        var expectedComment = new Comment(1L, 1L, "CommentTitle_10500");

        assertThat(commentService.findByBookId(expectedComment.getId()))
                .singleElement()
                .isNotEqualTo(expectedComment);

        var returnedComment = commentService.update(expectedComment.getId(), expectedComment.getText());
        assertThat(returnedComment).isNotNull()
                .matches(Comment -> Comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        assertThat(commentService.findByBookId(returnedComment.getId()))
                .singleElement()
                .isEqualTo(returnedComment);
    }

    @DisplayName("должен удалять коммент по id ")
    @Test
    void shouldDeleteComment() {
        assertThat(commentService.findByBookId(1L)).singleElement().isNotNull();
        commentService.deleteById(1L);
        assertThat(commentService.findByBookId(1L)).isEmpty();
    }
}