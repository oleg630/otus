package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.BookServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с книгами")
@DataJpaTest
@DirtiesContext
@Import({JpaBookRepository.class, JpaAuthorRepository.class, JpaGenreRepository.class, BookServiceImpl.class})
class BookServiceTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookService bookService;

    @DisplayName("должен загружать информацию о книге студенте по ее id")
    @Test
    void shouldFindExpectedStudentById() {
        Optional<Book> optionalActualBook = bookService.findById(1);
        Book expectedBook = em.find(Book.class, 1);
        assertThat(optionalActualBook).isPresent().get().isEqualToComparingFieldByField(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        List<Book> actualBooks = bookService.findAll();
        List<Book> expectedBooks = List.of(em.find(Book.class, 1),
                em.find(Book.class, 2), em.find(Book.class, 3));
        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);

        assertThat(actualBooks.get(0).getAuthor()).isNotNull();
        assertThat(actualBooks.get(0).getGenres().get(0).getName()).isNotNull();
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expectedBook = new Book(null, "BookTitle_10500", em.find(Author.class, 1),
                List.of(em.find(Genre.class, 1), em.find(Genre.class, 3)));
        var returnedBook = bookService.insert(expectedBook.getTitle(), expectedBook.getAuthor().getId(),
                expectedBook.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(bookService.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = new Book(1L, "BookTitle_10500", em.find(Author.class, 1),
                List.of(em.find(Genre.class, 1), em.find(Genre.class, 3)));

        assertThat(bookService.findById(expectedBook.getId()))
                .isPresent()
                .get()
                .isNotEqualTo(expectedBook);

        var returnedBook = bookService.update(expectedBook.getId(), expectedBook.getTitle(),
                expectedBook.getAuthor().getId(),
                expectedBook.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(bookService.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        assertThat(bookService.findById(1L)).isPresent();
        bookService.deleteById(1L);
        assertThat(bookService.findById(1L)).isEmpty();
    }
}