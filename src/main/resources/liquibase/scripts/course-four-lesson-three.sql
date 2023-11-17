-- liquibase formatted sql

-- changeset vKryatov:1
CREATE INDEX  students_name_index ON student(name);

-- changeset vKryatov:2
CREATE INDEX  faculties_name_and_color_index ON faculty(name, color);