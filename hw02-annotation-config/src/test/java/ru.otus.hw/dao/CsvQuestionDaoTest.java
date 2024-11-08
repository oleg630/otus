package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvQuestionDaoTest {

    @Test
    void testFileNotFound() {
        TestFileNameProvider fileNameProvider = new AppProperties(1, "test");
        QuestionReadException thrown = assertThrows(
                QuestionReadException.class,
                () -> new CsvQuestionDao(fileNameProvider)
        );

        assertTrue(thrown.getMessage().contains("file not found: test"));
    }

    @Test
    void testFindAll() {
        TestFileNameProvider fileNameProvider = new AppProperties(1, "test.csv");
        CsvQuestionDao csvQuestionDao = new CsvQuestionDao(fileNameProvider);

        assertNotNull(csvQuestionDao);

        List<Question> questions = csvQuestionDao.findAll();
        assertNotNull(questions);
        assertEquals(5, questions.size());
    }
}