package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {AppProperties.class})
@EnableConfigurationProperties(value = AppProperties.class)
class CsvQuestionDaoTest {

    @Autowired
    private AppProperties appProperties;

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
        CsvQuestionDao csvQuestionDao = new CsvQuestionDao(appProperties);

        assertNotNull(csvQuestionDao);

        List<Question> questions = csvQuestionDao.findAll();
        assertNotNull(questions);
        assertEquals(5, questions.size());
    }
}