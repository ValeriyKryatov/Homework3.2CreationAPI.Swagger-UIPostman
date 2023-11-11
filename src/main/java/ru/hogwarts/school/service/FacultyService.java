package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.*;

@Service
public class FacultyService {

    private final static Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        return facultyRepository.save(faculty);
    }

    public Faculty readFaculty(long id) {
        logger.info("Was invoked method for getting faculty by id = {}", id);
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty changeFaculty(Faculty faculty) {
        logger.info("Was invoked method for change faculty");
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(long id) {
        logger.info("Was invoked method for removing faculty by id = {}", id);
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getAllFaculties() {
        logger.info("Was invoked method for getting all faculties in school");
        return facultyRepository.findAll();
    }

    public Collection<Faculty> findByColor(String color) {
        logger.info("Was invoked method for getting faculty by color = {}", color);
        return facultyRepository.findByColorContainsIgnoreCase(color);
    }

    public Collection<Faculty> findByName(String name) {
        logger.info("Was invoked method for getting faculty by name = {}", name);
        return facultyRepository.findByNameContainsIgnoreCase(name);
    }
}