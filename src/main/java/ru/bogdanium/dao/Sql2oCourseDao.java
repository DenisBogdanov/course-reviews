package ru.bogdanium.dao;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.bogdanium.exception.DaoException;
import ru.bogdanium.model.Course;

import java.util.List;

public class Sql2oCourseDao implements CourseDao {

    private static final String SQL_ADD_COURSE = "INSERT INTO courses(name, url) VALUES (:name, :url)";
    private static final String SQL_GET_ALL_COURSES = "SELECT * FROM courses";
    private static final String SQL_GET_COURSE_BY_ID = "SELECT * FROM courses WHERE id = :id";

    private final Sql2o sql2o;

    public Sql2oCourseDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Course course) throws DaoException {

        try (Connection connection = sql2o.open()) {
            int key = (int) connection
                    .createQuery(SQL_ADD_COURSE)
                    .bind(course)
                    .executeUpdate()
                    .getKey();

            course.setId(key);

        } catch (Sql2oException e) {
            throw new DaoException(e, "Problem adding course");
        }
    }

    @Override
    public List<Course> findAll() {

        try (Connection connection = sql2o.open()) {
            return connection
                    .createQuery(SQL_GET_ALL_COURSES)
                    .executeAndFetch(Course.class);
        }
    }

    @Override
    public Course findById(int id) {
        try (Connection connection = sql2o.open()) {
            return connection
                    .createQuery(SQL_GET_COURSE_BY_ID)
                    .addParameter("id", id)
                    .executeAndFetchFirst(Course.class);
        }
    }
}
