package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CsvQuestionDaoTest {

    @Test
    void testFileNotFound() {
        AppProperties fileNameProvider = new AppProperties();
        fileNameProvider.setLocale("ru-RU");
        fileNameProvider.setFileNameByLocaleTag(Map.of("ru-RU", "test"));
        QuestionReadException thrown = assertThrows(
                QuestionReadException.class,
                () -> new CsvQuestionDao(fileNameProvider)
        );

        assertTrue(thrown.getMessage().contains("file not found! test"));
    }

    @Test
    void testFindAll() {
        TestFileNameProvider testFileNameProvider = mock(TestFileNameProvider.class);
        when(testFileNameProvider.getTestFileName()).thenReturn("test_ru.csv");
        CsvQuestionDao csvQuestionDao = new CsvQuestionDao(testFileNameProvider);

        assertNotNull(csvQuestionDao);

        List<Question> questions = csvQuestionDao.findAll();
        assertNotNull(questions);
        assertEquals(5, questions.size());
    }
}