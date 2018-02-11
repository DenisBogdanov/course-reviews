package ru.bogdanium.dao;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.bogdanium.exception.DaoException;
import ru.bogdanium.model.Course;

import java.util.List;

public class Sql2oCourseDao implements CourseDao {

    private final Sql2o sql2o;

    public Sql2oCourseDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Course course) throws DaoException {

        String sql = "INSERT INTO courses(name, url) VALUES (:name, :url)";
        try (Connection connection = sql2o.open()) {
            int key = (int) connection.createQuery(sql)
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
        return null;
    }
}
