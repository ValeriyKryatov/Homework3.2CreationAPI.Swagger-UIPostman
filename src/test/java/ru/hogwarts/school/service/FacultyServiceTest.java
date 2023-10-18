package ru.hogwarts.school.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.hogwarts.school.model.Faculty;

import static org.junit.jupiter.api.Assertions.*;

class FacultyServiceTest {

    FacultyService facultyService = new FacultyService();

    @Test
    void testCreateFaculty() {
        var result = facultyService.createFaculty(new Faculty(null, "Гриффиндор", "Красно-желтый"));
        assertEquals(result.getId(), 0);
        assertEquals(result.getName(), "Гриффиндор");
        assertEquals(result.getColor(), "Красно-желтый");
    }

    @Test
    void testReadFaculty() {
        var faculty = facultyService.createFaculty(new Faculty(null, "Слизерин", "Зеленый"));
        var result = facultyService.readFaculty(faculty.getId());
        assertEquals(result.getName(), "Слизерин");
        assertEquals(result.getColor(), "Зеленый");
    }

    @Test
    void testChangeFaculty() {
        var result1 = facultyService.changeFaculty(new Faculty(99999L, "Пуффендуй", "Синий"));
        assertNull(result1);

        var addedFaculty = facultyService.createFaculty(new Faculty(null, "Пуффендуй", "Синий"));
        addedFaculty.setName("Когтевран");
        addedFaculty.setColor("Голубой");

        var result2 = facultyService.changeFaculty(addedFaculty);
        assertEquals(result2.getName(), "Когтевран");
        assertEquals(result2.getColor(), "Голубой");
    }

    @Test
    void testFilterByColor() {
        var added1 = facultyService.createFaculty(new Faculty(null, "test1", "white"));
        var added2 = facultyService.createFaculty(new Faculty(null, "test2", "green"));
        var added3 = facultyService.createFaculty(new Faculty(null, "test3", "green"));

        var result = facultyService.findByColor("green");
        Assertions.assertThat(result).containsExactly(added2, added3);
    }

    @Test
    void testRemoveFaculty() {
        var faculty = facultyService.createFaculty(new Faculty(null, "test1", "white"));
        assertNotNull(facultyService.readFaculty(faculty.getId()));
    }
}