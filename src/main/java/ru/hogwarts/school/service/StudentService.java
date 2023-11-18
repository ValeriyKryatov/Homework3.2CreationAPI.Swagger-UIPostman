package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final static Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        return studentRepository.save(student);
    }

    public Student readStudent(long id) {
        logger.info("Was invoked method for getting student by id = {}", id);
        return studentRepository.findById(id).orElse(null);
    }

    public Student changeStudent(Student student) {
        logger.info("Was invoked method for change student");
        return studentRepository.save(student);
    }

    public void deleteStudent(long id) {
        logger.info("Was invoked method for removing student by id = {}", id);
        studentRepository.deleteById(id);
    }

    public Collection<Student> getAllStudents() {
        logger.info("Was invoked method for getting all students in school");
        return studentRepository.findAll();
    }

    public Collection<Student> findByAge(int age) {
        logger.info("Was invoked method for getting students by age = {}", age);
        logger.warn("The students age must be > 0!");
        return studentRepository.findByAge(age);
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        logger.info("Was invoked method for getting students whose age is between the min = {} and max = {} value", min, max);
        return studentRepository.findByAgeBetween(min, max);
    }

    public long studentsQuantity() {
        logger.info("Was invoked method for getting number of students in school");
        return studentRepository.getQuantityStudents();
    }

    public double studentsAverageAge() {
        logger.info("Was invoked method for getting average age of students in school");
        return studentRepository.getAverageAge();
    }

    public Collection<Student> lastFiveStudents() {
        logger.info("Was invoked method for getting last five students on the list");
        return studentRepository.lastFiveStudents();
    }

    public Collection<Student> studentNameWithLetterA() {
        logger.info("Was invoked method for getting alphabetically sorted uppercase names of all students whose name begins with the letter A");
        return studentRepository.findAll()
                .stream()
                .filter(student -> student.getName().toUpperCase().startsWith("А"))
                .peek(student -> student.setName(student.getName().toUpperCase()))
                .sorted(Comparator.comparing(Student::getName))
                .collect(Collectors.toList());
    }

    public double averageAgeStudentsInSchoolWithStreams() {
        logger.info("Was invoked method for getting average age students in school with streams");
        return studentRepository.findAll()
                .stream()
                .mapToInt(Student::getAge)
                .average().orElse(0);
    }

    public void getStudentNamesToConsole() {
        List<String> names = getNamesStudents1();
        System.out.println(names.get(0));
        System.out.println(names.get(1));

        Thread thread1 = new Thread(() -> {
            System.out.println(names.get(2));
            System.out.println(names.get(3));
        });

        Thread thread2 = new Thread(() -> {
            System.out.println(names.get(4));
            System.out.println(names.get(5));
            System.out.println("*********************");
        });

        thread1.start();
        thread2.start();
    }


    public List<String> getNamesStudents1() {
        return studentRepository.findAll()
                .stream()
                .map(Student::getName)
                .collect(Collectors.toList());
    }

    public void getStudentNamesToConsoleSynchronized() {
        List<String> names = getNamesStudents2();
        printStudentNamesSynchron(names.get(0));
        printStudentNamesSynchron(names.get(1));

        Thread thread1 = new Thread(() -> {
            printStudentNamesSynchron(names.get(2));
            printStudentNamesSynchron(names.get(3));
        });

        Thread thread2 = new Thread(() -> {
            printStudentNamesSynchron(names.get(4));
            printStudentNamesSynchron(names.get(5));
        });

        thread1.start();
        thread2.start();
    }

    public List<String> getNamesStudents2() {
        return studentRepository.findAll()
                .stream()
                .map(Student::getName)
                .collect(Collectors.toList());
    }

    private synchronized void printStudentNamesSynchron(Object o) {
        System.out.println(o.toString());
    }
}