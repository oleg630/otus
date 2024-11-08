package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Component
public class CsvQuestionDao implements QuestionDao {

    private final List<Question> questions;

    public CsvQuestionDao(TestFileNameProvider fileNameProvider) {
        // Использовать CsvToBean
        // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
        // Использовать QuestionReadException
        // Про ресурсы: https://mkyong.com/java/java-read-a-file-from-resources-folder/

        try (FileReader fileReader = new FileReader(getFileFromResource(fileNameProvider.getTestFileName()))) {
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

    private File getFileFromResource(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new QuestionReadException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }
    }

    @Override
    public List<Question> findAll() {
        return questions;
    }
}
