package ru.otus.hw.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {AppProperties.class})
@EnableConfigurationProperties(value = AppProperties.class)
class AppPropertiesTest {

    @Autowired
    private AppProperties appProperties;

    @Test
    void getRightAnswersCountToPass() {
        assertEquals(10, appProperties.getRightAnswersCountToPass());
    }

    @Test
    void getTestFileName() {
        assertEquals("test.csv", appProperties.getTestFileName());
    }

    @Test
    void getLocale() {
        assertEquals("US", appProperties.getLocale().getCountry());
        assertEquals("en", appProperties.getLocale().getLanguage());
    }
}