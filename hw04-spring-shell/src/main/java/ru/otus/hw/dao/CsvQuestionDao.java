package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvQuestionDao implements QuestionDao {

    private final List<Question> questions;

    public CsvQuestionDao(TestFileNameProvider fileNameProvider) {
        // Использовать CsvToBean
        // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
        // Использовать QuestionReadException
        // Про ресурсы: https://mkyong.com/java/java-read-a-file-from-resources-folder/

        try (InputStreamReader streamReader = new InputStreamReader(
                getFileFromResourceAsStream(fileNameProvider.getTestFileName()), StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(streamReader)
        ) {
            List<QuestionDto> questionDtos = new CsvToBeanBuilder(bufferedReader)
                    .withType(QuestionDto.class)
                    .withSeparator(';')
                    .withSkipLines(1)
                    .build().parse();

            questions = questionDtos.stream().map(QuestionDto::toDomainObject).toList();
        } catch (IOException e) {
            throw new QuestionReadException(e.getMessage());
        }
    }

    private InputStream getFileFromResourceAsStream(String fileName) {
        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        // the stream holding the file content
        if (inputStream == null) {
            throw new QuestionReadException("file not found! " + fileName);
        } else {
            return inputStream;
        }
    }

    @Override
    public List<Question> findAll() {
        return questions;
    }
}
