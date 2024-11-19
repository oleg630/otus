package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {AppProperties.class})
@EnableConfigurationProperties(value = AppProperties.class)
public class CsvQuestionDaoTest {

    @Autowired
    private AppProperties appProperties;

    @Test
    void testFileNotFound() {
        appProperties.setLocale("en-US");
        QuestionReadException thrown = assertThrows(
                QuestionReadException.class,
                () -> new CsvQuestionDao(appProperties)
        );

        assertTrue(thrown.getMessage().contains("file not found! test"));
    }

    @Test
    void testFindAll() {
        appProperties.setLocale("ru-RU");
        CsvQuestionDao csvQuestionDao = new CsvQuestionDao(appProperties);

        assertNotNull(csvQuestionDao);

        List<Question> questions = csvQuestionDao.findAll();
        assertNotNull(questions);
        assertEquals(5, questions.size());
    }
}