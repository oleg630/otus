package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookConverter {
    private final AuthorConverter authorConverter;

    private final GenreConverter genreConverter;

    public String bookToString(Book book) {
        var genresString = book.getGenres() == null ? "" :
                book.getGenres().stream()
                .map(genreConverter::genreToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        var commentsString = book.getBookComments() == null ? "" :
                book.getBookComments().stream()
                        .map(BookComment::getText)
                        .collect(Collectors.joining(", "));
        return "Id: %d, title: %s, author: {%s}, genres: [%s], comments: [%s]".formatted(
                book.getId(),
                book.getTitle(),
                authorConverter.authorToString(book.getAuthor()),
                genresString,
                commentsString);
    }
}
