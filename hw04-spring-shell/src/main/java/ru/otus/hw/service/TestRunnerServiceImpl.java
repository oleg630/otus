package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Student;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@ShellComponent
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    private final LocalizedIOService ioService;

    private final Map<String, Student> students = new HashMap<>();

    @Override
    public void run() {
        var student = studentService.determineCurrentStudent();
        var testResult = testService.executeTestFor(student);
        resultService.showResult(testResult);
    }

    @ShellMethod(key = "student-add", value = "Add new student")
    public String addStudent() {
        Student student = studentService.determineCurrentStudent();
        students.put(student.lastName(), student);
        return student.lastName();
    }

    @ShellMethod(key = "student-list", value = "List all students")
    public void listStudents() {
        ioService.printLine(students.values().toString());
    }

    @ShellMethod(key = "student-test", value = "Test student")
    public void testStudent(@ShellOption({"lastname", "ln"}) String lastname) {
        Student student = students.get(lastname);
        if (student == null) {
            throw new RuntimeException("Student not found");
        }

        var testResult = testService.executeTestFor(student);
        resultService.showResult(testResult);
    }

    @ShellMethod(key = "student-remove", value = "Remove student")
    public void removeStudent(@ShellOption({"lastname", "ln"}) String lastname) {
        Student student = students.get(lastname);
        if (student == null) {
            throw new RuntimeException("Student not found");
        }

        students.remove(lastname);
    }
}
