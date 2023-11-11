package ru.hogwarts.school;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import java.util.Collection;

import static ru.hogwarts.school.SchoolApplicationStudentControllerTest.faculty;
import static ru.hogwarts.school.SchoolApplicationStudentControllerTest.student;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SchoolApplicationFacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultycontroller;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void contextLoads() throws Exception {
        Assertions.assertThat(facultycontroller).isNotNull();
    }

    @Test
    public void testGetFacultyInfo() throws Exception {
        var facultyByTest = faculty("Гриффиндор", "Красно-желтый");
        var createdFacultyByTest = restTemplate.postForObject("http://localhost:" + port + "/faculty", facultyByTest, Faculty.class);
        var result = restTemplate.getForObject("http://localhost:" + port + "/faculty/" + createdFacultyByTest.getId(), Faculty.class);

        Assertions.assertThat(result.getName()).isEqualTo("Гриффиндор");
        Assertions.assertThat(result.getColor()).isEqualTo("Красно-желтый");

        restTemplate.delete("/faculty/" + result.getId());
    }

    @Test
    public void testGetAllFaculties() throws Exception {
        Assertions
                .assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/faculty", String.class))
                .isNotNull();
    }

    @Test
    public void testFindFacultiesByColor() throws Exception {
        var f1 = restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty("test1", "red"), Faculty.class);
        var f2 = restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty("test2", "green"), Faculty.class);
        var f3 = restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty("test3", "black"), Faculty.class);
        var f4 = restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty("test4", "blue"), Faculty.class);
        var f5 = restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty("test5", "red"), Faculty.class);

        var result = restTemplate.exchange("/faculty/color?color=red",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Faculty>>() {
                });

        var faculties = result.getBody();

        Assertions.assertThat(faculties).isNotNull();
        Assertions.assertThat(faculties.size()).isEqualTo(2);
        Assertions.assertThat(faculties).containsExactly(f1, f5);

        restTemplate.delete("/faculty/" + f1.getId());
        restTemplate.delete("/faculty/" + f2.getId());
        restTemplate.delete("/faculty/" + f3.getId());
        restTemplate.delete("/faculty/" + f4.getId());
        restTemplate.delete("/faculty/" + f5.getId());
    }

    @Test
    public void testFindFacultiesByName() throws Exception {
        var f1 = restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty("test1", "red"), Faculty.class);
        var f2 = restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty("test2", "green"), Faculty.class);
        var f3 = restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty("test3", "black"), Faculty.class);
        var f4 = restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty("test4", "blue"), Faculty.class);
        var f5 = restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty("test1", "red"), Faculty.class);

        var result = restTemplate.exchange("/faculty/name?name=test1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Faculty>>() {
                });

        var faculties = result.getBody();

        Assertions.assertThat(faculties).isNotNull();
        Assertions.assertThat(faculties.size()).isEqualTo(2);
        Assertions.assertThat(faculties).containsExactly(f1, f5);

        restTemplate.delete("/faculty/" + f1.getId());
        restTemplate.delete("/faculty/" + f2.getId());
        restTemplate.delete("/faculty/" + f3.getId());
        restTemplate.delete("/faculty/" + f4.getId());
        restTemplate.delete("/faculty/" + f5.getId());
    }

    @Test
    public void testFacultyByStudent() throws Exception {
        var createdFacultyByTest = restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty("Гриффиндор", "Красно-желтый"), Faculty.class);
        var s1 = student("Test1", 66);
        var s2 = student("Test2", 67);
        var s3 = student("Test3", 68);
        var s4 = student("Test4", 69);
        var s5 = student("Test5", 68);

        s1.setFaculty(createdFacultyByTest);
        s2.setFaculty(createdFacultyByTest);
        s3.setFaculty(createdFacultyByTest);
        s4.setFaculty(createdFacultyByTest);
        s5.setFaculty(createdFacultyByTest);

        var createdStudent1 = restTemplate.postForObject("/student", s1, Student.class);
        var createdStudent2 = restTemplate.postForObject("/student", s2, Student.class);
        var createdStudent3 = restTemplate.postForObject("/student", s3, Student.class);
        var createdStudent4 = restTemplate.postForObject("/student", s4, Student.class);
        var createdStudent5 = restTemplate.postForObject("/student", s5, Student.class);

        var result = restTemplate.exchange("http://localhost:" + port + "/faculty/" + createdFacultyByTest.getId() + "/students",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Student>>() {
                });

        var students = result.getBody();

        Assertions.assertThat(students).isNotNull();
        Assertions.assertThat(students.size()).isNotNull();
        Assertions.assertThat(students).contains(createdStudent1, createdStudent2, createdStudent3,createdStudent4, createdStudent5);

        restTemplate.delete("/student/" + createdStudent1.getId());
        restTemplate.delete("/student/" + createdStudent2.getId());
        restTemplate.delete("/student/" + createdStudent3.getId());
        restTemplate.delete("/student/" + createdStudent4.getId());
        restTemplate.delete("/student/" + createdStudent5.getId());
        restTemplate.delete("/faculty/" + createdFacultyByTest.getId());
    }

    @Test
    public void testCreateFaculty() throws Exception {
        var facultyByTest = faculty("Гриффиндор", "Красно-желтый");
        var result = restTemplate.postForObject("/faculty", facultyByTest, Faculty.class);
        System.out.println(result);
        Assertions.assertThat(result.getColor()).isEqualTo("Красно-желтый");
        Assertions.assertThat(result.getName()).isEqualTo("Гриффиндор");
        Assertions.assertThat(result.getId()).isNotNull();
        restTemplate.delete("/faculty/" + result.getId());
    }

    @Test
    public void testChangeFaculty() throws Exception {
        var facultyByTest = faculty("Слизерин", "Зеленый");
        var createdFacultyByTest = restTemplate.postForObject("http://localhost:" + port + "/faculty", facultyByTest, Faculty.class);
        createdFacultyByTest.setName("Гриффиндор");
        createdFacultyByTest.setColor("Красно-желтый");

        ResponseEntity<Faculty> facultyEntity = restTemplate.exchange(
                "http://localhost:" + port + "/faculty",
                HttpMethod.PUT,
                new HttpEntity<>(createdFacultyByTest),
                Faculty.class);

        Assertions.assertThat(facultyEntity.getBody().getName()).isEqualTo("Гриффиндор");
        Assertions.assertThat(facultyEntity.getBody().getColor()).isEqualTo("Красно-желтый");

        restTemplate.delete("/faculty/" + facultyEntity.getBody().getId());
    }

    @Test
    public void testDeleteFaculty() throws Exception {
        var facultyByTest = faculty("Слизерин", "Зеленый");
        var createdFacultyByTest = restTemplate.postForObject("/faculty", facultyByTest, Faculty.class);
        ResponseEntity<Faculty> facultyEntity = restTemplate.exchange(
                "/faculty/" + createdFacultyByTest.getId(),
                HttpMethod.DELETE,
                null,
                Faculty.class);
        Assertions.assertThat(facultyEntity.getBody().getName()).isEqualTo("Слизерин");
        Assertions.assertThat(facultyEntity.getBody().getColor().equals("Зеленый"));
        var deletedFaculty = restTemplate.getForObject("/faculty/" + createdFacultyByTest.getId(), Faculty.class);
        Assertions.assertThat(deletedFaculty).isNull();
    }

    public static Student student(String name, int age) {
        var s = new Student();
        s.setName(name);
        s.setAge(age);
        return s;
    }

    public static Faculty faculty(String name, String color) {
        var f = new Faculty();
        f.setName(name);
        f.setColor(color);
        return f;
    }
}