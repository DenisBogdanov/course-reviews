package ru.bogdanium.dao;

import ru.bogdanium.exception.DaoException;
import ru.bogdanium.model.Course;

import java.util.List;

public interface CourseDao {
    void add(Course course) throws DaoException;

    List<Course> findAll();

    Course findById(int id);
}
