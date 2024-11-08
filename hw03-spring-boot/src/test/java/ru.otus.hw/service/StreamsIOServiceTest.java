package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class StreamsIOServiceTest {

    PrintStream printStream;

    StreamsIOService ioService;
    InputStream inputStream;

    @BeforeEach
    public void init() {
        printStream = mock(PrintStream.class);
        inputStream = mock(InputStream.class);
        ioService = new StreamsIOService(printStream, inputStream);
    }

    @Test
    void printLine() {
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);

        ioService.printLine("test");

        verify(printStream).println(argument.capture());
        assertEquals("test", argument.getValue());
    }

    @Test
    void printFormattedLine() {
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> secondArgument = ArgumentCaptor.forClass(String.class);

        ioService.printFormattedLine("test1", "test2");

        verify(printStream).printf(argument.capture(), secondArgument.capture());
        assertEquals("test1%n", argument.getValue());
        assertEquals("test2", secondArgument.getValue());
    }
}