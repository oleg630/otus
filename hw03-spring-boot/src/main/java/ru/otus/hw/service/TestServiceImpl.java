package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            var isAnswerValid = processQuestion(question);
            ioService.printLine(isAnswerValid ? ioService.getMessage("TestService.answer.correct")
                    : ioService.getMessage("TestService.answer.wrong"));
            ioService.printLine("");

            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private boolean processQuestion(Question question) {
        printQuestion(question, false);
        ioService.printLine("");
        int input = ioService.readIntForRangeWithPromptLocalized(1, question.answers().size(),
                "TestService.answer.number.get", "TestService.answer.number.wrong");

        return input > 0 && input <= question.answers().size() && question.answers().get(input - 1).isCorrect();
    }

    private void printQuestion(Question question, boolean showAnswers) {
        ioService.printLineLocalized("TestService.question");
        ioService.printLine(question.text());
        int i = 1;
        for (Answer answer : question.answers()) {
            ioService.printFormattedLineLocalized("TestService.answer.print", i++, showAnswers && answer.isCorrect() ?
                    ioService.getMessage("TestService.answer.correct") : "");
            ioService.printLine(answer.text());
        }
    }
}
