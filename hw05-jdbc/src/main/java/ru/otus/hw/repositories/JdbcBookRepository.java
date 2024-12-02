package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final GenreRepository genreRepository;

    public JdbcBookRepository(DataSource dataSource, GenreRepository genreRepository) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.genreRepository = genreRepository;
    }

    @Override
    public Optional<Book> findById(long id) {
        String sql = "select b.id, b.title, b.author_id, a.full_name as author_name from books b " +
                "left join authors a on (a.id = b.author_id) where b.id = :book_id ";
        Map<String, Object> namedParameters = Collections.singletonMap("book_id", id);

        Book book = jdbcTemplate.query(sql, namedParameters, new BookResultSetExtractor());
        if (book != null) {
            var genres = genreRepository.findAll();
            var relations = getBookGenreRelations(book.getId());
            mergeBooksInfo(List.of(book), genres, relations);
            return Optional.of(book);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        var books = getAllBooksWithoutGenres();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        removeGenresRelationsFor(id);

        Map<String, Object> params = Collections.singletonMap("id", id);
        int updated = jdbcTemplate.update(
                "delete from books where id = :id", params
        );
        if (updated == 0) {
            throw new EntityNotFoundException("Entity with ID: " + id + " not found");
        }
    }

    private List<Book> getAllBooksWithoutGenres() {
        return jdbcTemplate.query(
                "select b.id, b.title, b.author_id, a.full_name as author_name from books b " +
                        "left join authors a on (a.id = b.author_id) ",
                new JdbcBookRepository.BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        String sql = "select book_id, genre_id from books_genres";
        return jdbcTemplate.query(sql, new BookGenreRowMapper());
    }

    private List<BookGenreRelation> getBookGenreRelations(Long bookId) {
        String sql = "select book_id, genre_id from books_genres where book_id = :book_id";
        Map<String, Object> params = Collections.singletonMap("book_id", bookId);
        return jdbcTemplate.query(sql, params, new BookGenreRowMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        // Добавить книгам (booksWithoutGenres) жанры (genres) в соответствии со связями (relations)
        Map<Long, Genre> genresMap = genres.stream().collect(Collectors.toMap(Genre::getId, g -> g));

        for (Book book : booksWithoutGenres) {
            book.setGenres(relations.stream().filter(r -> r.bookId() == book.getId())
                    .map(r -> genresMap.get(r.genreId())).toList());
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor() != null ? book.getAuthor().getId() : 0);
        jdbcTemplate.update(
                "insert into books (title, author_id) VALUES (:title, :author_id)", params, keyHolder
        );

        book.setId(keyHolder.getKeyAs(Long.class));
        //noinspection DataFlowIssue
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", book.getId());
        params.put("title", book.getTitle());
        params.put("author_id", book.getAuthor() != null ? book.getAuthor().getId() : 0);
        int updated = jdbcTemplate.update(
                "update books set title = :title, author_id = :author_id where id = :id", params
        );

        if (updated == 0) {
            throw new EntityNotFoundException("Entity with ID: " + book.getId() + " not found");
        }

        removeGenresRelationsFor(book.getId());
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        if (book.getGenres() == null) {
            return;
        }

        List<Object[]> batch = new ArrayList<>();
        for (Genre genre : book.getGenres()) {
            Object[] values = new Object[]{book.getId(), genre.getId()};
            batch.add(values);
        }
        jdbcTemplate.getJdbcOperations().batchUpdate(
                "insert into books_genres (book_id, genre_id) VALUES(?, ?)", batch);
    }

    private void removeGenresRelationsFor(long bookId) {
        Map<String, Object> params = Collections.singletonMap("book_id", bookId);
        jdbcTemplate.update(
                "delete from books_genres where book_id = :book_id", params
        );
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setTitle(rs.getString("title"));

            Author author = new Author();
            author.setId(rs.getLong("author_id"));
            author.setFullName(rs.getString("author_name"));
            book.setAuthor(author);
            return book;
        }
    }

    // Использовать для findById
    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (!rs.first()) {
                return null;
            }

            Book book = new Book();
            book.setId(rs.getLong("id"));
            book.setTitle(rs.getString("title"));

            Author author = new Author();
            author.setId(rs.getLong("author_id"));
            author.setFullName(rs.getString("author_name"));
            book.setAuthor(author);
            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }

    private static class BookGenreRowMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id"));
        }
    }
}
