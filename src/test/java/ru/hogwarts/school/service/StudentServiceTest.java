package ru.hogwarts.school.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hogwarts.school.model.Student;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {

    StudentService studentService = new StudentService();

    @Test
    void testCreateStudent() {
        var result = studentService.createStudent(new Student(null, "Гарри", 15));
        assertEquals(result.getId(), 0);
        assertEquals(result.getName(), "Гарри");
        assertEquals(result.getAge(), 15);
    }

    @Test
    void testReadStudent() {
        var student = studentService.createStudent(new Student(null, "Гермиона", 16));
        var result = studentService.readStudent(student.getId());
        assertEquals(result.getName(), "Гермиона");
        assertEquals(result.getAge(), 16);
    }

    @Test
    void testChangeStudent() {
        var result1 = studentService.changeStudent(new Student(99999L, "Рон", 17));
        assertNull(result1);

        var addedStudent = studentService.createStudent(new Student(null, "Рон", 17));
        addedStudent.setName("Невил");
        addedStudent.setAge(12);

        var result2 = studentService.changeStudent(addedStudent);
        assertEquals(result2.getName(), "Невил");
        assertEquals(result2.getAge(), 12);
    }

    @Test
    void testFilterByAge() {
        var added1 = studentService.createStudent(new Student(null, "test1", 11));
        var added2 = studentService.createStudent(new Student(null, "test2", 13));
        var added3 = studentService.createStudent(new Student(null, "test3", 13));

        var result = studentService.findByAge(13);
        Assertions.assertThat(result).containsExactly(added2, added3);
    }

    @Test
    void testRemoveStudent() {
        var student = studentService.createStudent(new Student(null, "test1", 18));
        assertNotNull(studentService.readStudent(student.getId()));
    }
}