package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

public class CsvQuestionDao implements QuestionDao {

    private final List<Question> questions;

    public CsvQuestionDao(TestFileNameProvider fileNameProvider) {
        // Использовать CsvToBean
        // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
        // Использовать QuestionReadException
        // Про ресурсы: https://mkyong.com/java/java-read-a-file-from-resources-folder/

        URL resourceUrl = getClass().getClassLoader().getResource(fileNameProvider.getTestFileName());
        if (resourceUrl == null) {
            throw new QuestionReadException("file not found: " + fileNameProvider.getTestFileName());
        }
        try (FileReader fileReader = new FileReader(Paths.get(resourceUrl.toURI()).toFile())) {
            List<QuestionDto> questionDtos = new CsvToBeanBuilder(fileReader)
                    .withType(QuestionDto.class)
                    .withSeparator(';')
                    .withSkipLines(1)
                    .build().parse();

            questions = questionDtos.stream().map(QuestionDto::toDomainObject).toList();
        } catch (IOException | URISyntaxException e) {
            throw new QuestionReadException(e.getMessage());
        }
    }

    @Override
    public List<Question> findAll() {
        return questions;
    }
}
