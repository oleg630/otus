package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printFormattedLine("%nPlease answer the questions below");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            var isAnswerValid = processQuestion(question);
            ioService.printLine(isAnswerValid ? "<Correct>" : "<Wrong>");
            ioService.printLine("");

            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private boolean processQuestion(Question question) {
        printQuestion(question, false);
        ioService.printLine("");
        int input = ioService.readIntForRangeWithPrompt(1, question.answers().size(),
                "please enter answer number:", "wrong number");

        for (int i = 0; i < question.answers().size(); i++) {
            if (question.answers().get(i).isCorrect() && (input == i + 1)) {
                return true;
            }
        }
        return false;
    }

    private void printQuestion(Question question, boolean showAnswers) {
        ioService.printLine("Question:");
        ioService.printLine(question.text());
        int i = 1;
        for (Answer answer : question.answers()) {
            ioService.printFormattedLine("Answer %s: %s", i++, showAnswers && answer.isCorrect() ? "<Correct>" : "");
            ioService.printLine(answer.text());
        }
    }
}
