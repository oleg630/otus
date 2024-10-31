package ru.otus.hw.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppPropertiesTest {

    @Test
    void getRightAnswersCountToPass() {
        TestConfig testConfig = new AppProperties(10, "test");
        assertEquals(10, testConfig.getRightAnswersCountToPass());
    }

    @Test
    void getTestFileName() {
        TestFileNameProvider fileNameProvider = new AppProperties(10, "test");
        assertEquals("test", fileNameProvider.getTestFileName());
    }
}