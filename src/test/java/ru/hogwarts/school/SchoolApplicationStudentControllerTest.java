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
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SchoolApplicationStudentControllerTest {

	@LocalServerPort
	private int port;

	@Autowired
	private StudentController studentController;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void contextLoads() throws Exception {
		Assertions.assertThat(studentController).isNotNull();
	}

	@Test
	public void testGetStudentInfo() throws Exception {
		var studentByTest = student("Гарри", 18);
		var createdStudentByTest = restTemplate.postForObject("http://localhost:" + port + "/student", studentByTest, Student.class);
		var result = restTemplate.getForObject("http://localhost:" + port + "/student/" + createdStudentByTest.getId(), Student.class);

		Assertions.assertThat(result.getName()).isEqualTo("Гарри");
		Assertions.assertThat(result.getAge()).isEqualTo(18);

		restTemplate.delete("/student/" + result.getId());
	}

	@Test
	public void testGetAllStudents() throws Exception {
		Assertions
				.assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/student", String.class))
				.isNotNull();
	}

	@Test
	public void testFindStudents() throws Exception {
		var s1 = restTemplate.postForObject("http://localhost:" + port + "/student", student("test1", 44), Student.class);
		var s2 = restTemplate.postForObject("http://localhost:" + port + "/student", student("test2", 45), Student.class);
		var s3 = restTemplate.postForObject("http://localhost:" + port + "/student", student("test3", 46), Student.class);
		var s4 = restTemplate.postForObject("http://localhost:" + port + "/student", student("test4", 47), Student.class);
		var s5 = restTemplate.postForObject("http://localhost:" + port + "/student", student("test5", 44), Student.class);

		var result = restTemplate.exchange("/student/age?age=44",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<Collection<Student>>() {
				});

		var students = result.getBody();

		Assertions.assertThat(students).isNotNull();
		Assertions.assertThat(students.size()).isEqualTo(2);
		Assertions.assertThat(students).containsExactly(s1, s5);

		restTemplate.delete("/student/" + s1.getId());
		restTemplate.delete("/student/" + s2.getId());
		restTemplate.delete("/student/" + s3.getId());
		restTemplate.delete("/student/" + s4.getId());
		restTemplate.delete("/student/" + s5.getId());
	}

	@Test
	public void testFindStudentsByAgeBetween() throws Exception {
		var s1 = restTemplate.postForObject("http://localhost:" + port + "/student", student("test1", 50), Student.class);
		var s2 = restTemplate.postForObject("http://localhost:" + port + "/student", student("test2", 51), Student.class);
		var s3 = restTemplate.postForObject("http://localhost:" + port + "/student", student("test3", 52), Student.class);
		var s4 = restTemplate.postForObject("http://localhost:" + port + "/student", student("test4", 53), Student.class);
		var s5 = restTemplate.postForObject("http://localhost:" + port + "/student", student("test5", 54), Student.class);

		var result = restTemplate.exchange("/student/age_between?min=51&max=53",
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<Collection<Student>>() {
				});

		var students = result.getBody();

		Assertions.assertThat(students).isNotNull();
		Assertions.assertThat(students.size()).isEqualTo(3);
		Assertions.assertThat(students).containsExactly(s2, s3, s4);

		restTemplate.delete("/student/" + s1.getId());
		restTemplate.delete("/student/" + s2.getId());
		restTemplate.delete("/student/" + s3.getId());
		restTemplate.delete("/student/" + s4.getId());
		restTemplate.delete("/student/" + s5.getId());
	}

	@Test
	public void testFindByFaculty() throws Exception {
		var studentByTest = student("Рон", 21);
		var createdFacultyByTest = restTemplate.postForObject("http://localhost:" + port + "/faculty", faculty("testName", "testColor"), Faculty.class);
		studentByTest.setFaculty(createdFacultyByTest);
		var createdStudentByTest = restTemplate.postForObject("http://localhost:" + port + "/student", studentByTest, Student.class);

		var result = restTemplate.getForObject("http://localhost:" + port + "/student/" + createdStudentByTest.getId() + "/faculty", Faculty.class);

		Assertions.assertThat(result).isNotNull();
		Assertions.assertThat(result.getName()).isEqualTo("testName");
		Assertions.assertThat(result.getColor()).isEqualTo("testColor");

		restTemplate.delete("/student/" + createdStudentByTest.getId());
		restTemplate.delete("/faculty/" + createdFacultyByTest.getId());
	}

	@Test
	public void testCreateStudent() throws Exception {
		var studentByTest = student("Рон", 21);
		var result = restTemplate.postForObject("http://localhost:" + port + "/student", studentByTest, Student.class);

		Assertions.assertThat(result.getAge()).isEqualTo(21);
		Assertions.assertThat(result.getName()).isEqualTo("Рон");
		Assertions.assertThat(result.getId()).isNotNull();

		restTemplate.delete("/student/" + result.getId());
	}

	@Test
	public void testChangeStudent() throws Exception {
		var studentByTest = student("Гарри", 18);
		var createdStudentByTest = restTemplate.postForObject("http://localhost:" + port + "/student", studentByTest, Student.class);
		createdStudentByTest.setName("Гермиона");
		createdStudentByTest.setAge(22);

		ResponseEntity<Student> studentEntity = restTemplate.exchange(
				"http://localhost:" + port + "/student",
				HttpMethod.PUT,
				new HttpEntity<>(createdStudentByTest),
				Student.class);

		Assertions.assertThat(studentEntity.getBody().getName()).isEqualTo("Гермиона");
		Assertions.assertThat(studentEntity.getBody().getAge()).isEqualTo(22);

		restTemplate.delete("/student/" + studentEntity.getBody().getId());
	}

	@Test
	public void testDeleteStudent() {
		var studentByTest = student("DeletedStudent", 99);
		var createdStudentByTest = restTemplate.postForObject("http://localhost:" + port + "/student", studentByTest, Student.class);

		ResponseEntity<Student> studentEntity = restTemplate.exchange(
				"http://localhost:" + port + "/student/" + createdStudentByTest.getId(),
				HttpMethod.DELETE,
				null,
				Student.class);

		Assertions.assertThat(studentEntity.getBody().getName()).isEqualTo("DeletedStudent");
		Assertions.assertThat(studentEntity.getBody().getAge()).isEqualTo(99);

		var deletedStudent = restTemplate.getForObject("http://localhost:" + port + "/student/" + createdStudentByTest.getId(), Student.class);
		Assertions.assertThat(deletedStudent).isNull();
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