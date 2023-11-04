package ru.hogwarts.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentService studentService;
    private final FacultyService facultyService;

    public StudentController(StudentService studentService, FacultyService facultyService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentInfo(@PathVariable Long id) {
        Student student = studentService.readStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @GetMapping
    public ResponseEntity<Collection<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/age")
    public ResponseEntity<Collection<Student>> findStudents(@RequestParam int age) {
        if (age > 0) {
            return ResponseEntity.ok(studentService.findByAge(age));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/age_between")
    public ResponseEntity<Collection<Student>> findStudentsByAgeBetween(@RequestParam int min,
                                                                        @RequestParam int max) {
        return ResponseEntity.ok(studentService.findByAgeBetween(min, max));
    }

    @GetMapping("/{studentId}/faculty")
    public Faculty findByFaculty(@PathVariable long studentId) {
        return studentService.readStudent(studentId).getFaculty();
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @PutMapping
    public ResponseEntity<Student> changeStudent(@RequestBody Student student) {
        Student foundStudent = studentService.changeStudent(student);
        if (foundStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Student> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/quantity_students")
    public long getQuantityStudentsInSchool() {
        return studentService.studentsQuantity();
    }

    @GetMapping("/average_age")
    public double getAverageAgeStudentsInSchool() {
        return studentService.studentsAverageAge();
    }

    @GetMapping("/lastFiveStudents")
    public Collection<Student> getLastFiveStudents() {
        return studentService.lastFiveStudents();
    }
}