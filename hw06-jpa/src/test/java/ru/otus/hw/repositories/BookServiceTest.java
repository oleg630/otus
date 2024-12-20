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
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.BookServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Сервис для работы с книгами")
@DataJpaTest
@DirtiesContext
@Transactional(propagation = Propagation.NEVER)
@Import({JpaBookRepository.class, JpaAuthorRepository.class, JpaGenreRepository.class, BookServiceImpl.class})
class BookServiceTest {

    @Autowired
    private BookService bookService;

    private final List<Book> bookList;

    BookServiceTest() {
        bookList = List.of(
                new Book(1L, "BookTitle_1", new Author(1L, "Author_1"),
                        List.of(new Genre(1L, "Genre_1"), new Genre(2L, "Genre_2"))),
                new Book(2L, "BookTitle_2", new Author(2L, "Author_2"),
                        List.of(new Genre(3L, "Genre_3"), new Genre(4L, "Genre_4"))),
                new Book(3L, "BookTitle_3", new Author(3L, "Author_3"),
                        List.of(new Genre(5L, "Genre_5"), new Genre(6L, "Genre_6")))
        );
    }

    @DisplayName("должен загружать информацию о книге по ее id")
    @Test
    void shouldFindExpectedBookById() {
        Book expectedBook = bookList.get(0);
        Optional<Book> optionalActualBook = bookService.findById(expectedBook.getId());
        compareTwoBooks(expectedBook, optionalActualBook.orElse(null));
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        List<Book> actualBooks = bookService.findAll();
        List<Book> expectedBooks = bookList;
        for (int i = 0; i < expectedBooks.size(); i++) {
            compareTwoBooks(expectedBooks.get(i), actualBooks.get(i));
        }
        assertThat(actualBooks.get(0).getAuthor().getFullName()).isNotNull();
        assertThat(actualBooks.get(0).getGenres().get(0).getName()).isNotNull();
    }

    @DisplayName("должен сохранять новую книгу, а потом удалять")
    @Test
    void shouldSaveNewBookAndDelete() {
        var expectedBook = bookList.get(1);
        expectedBook.setId(null);
        var returnedBook = bookService.insert(expectedBook.getTitle(), expectedBook.getAuthor().getId(),
                expectedBook.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()));
        compareTwoBooks(expectedBook, returnedBook);

        compareTwoBooks(returnedBook, bookService.findById(returnedBook.getId()).orElse(null));

        bookService.deleteById(returnedBook.getId());
        assertThat(bookService.findById(returnedBook.getId())).isEmpty();
    }


    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expected = new Book(2L, "new title", new Author(3L, "Author_3"),
                List.of(new Genre(1L, "Genre_1"), new Genre(6L, "Genre_6")));

        Optional<Book> actual = bookService.findById(expected.getId());
        assertTrue(actual.isPresent());
        assertNotEquals(expected.getTitle(), actual.get().getTitle());
        assertNotEquals(expected.getAuthor().getId(), actual.get().getAuthor().getId());
        assertThat(expected.getGenres().stream().map(Genre::getId).toArray())
                .isNotSameAs(actual.get().getGenres().stream().map(Genre::getId).toArray());

        var returnedBook = bookService.update(expected.getId(), expected.getTitle(),
                expected.getAuthor().getId(), expected.getGenres().stream().map(Genre::getId)
                        .collect(Collectors.toSet()));
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expected);

        compareTwoBooks(returnedBook, bookService.findById(returnedBook.getId()).orElse(null));

        // откат
        expected = bookList.get(1);
        bookService.update(expected.getId(), expected.getTitle(),
                expected.getAuthor().getId(), expected.getGenres().stream().map(Genre::getId)
                        .collect(Collectors.toSet()));
    }

    private void compareTwoBooks(Book expected, Book actual) {
        assertNotNull(actual);
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getAuthor().getId(), actual.getAuthor().getId());
        assertEquals(expected.getAuthor().getFullName(), actual.getAuthor().getFullName());
        assertThat(expected.getGenres().stream().map(Genre::getId).toArray())
                .containsExactly(actual.getGenres().stream().map(Genre::getId).toArray());
    }
}