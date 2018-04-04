package ru.bogdanium.dao;

import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.bogdanium.exception.DaoException;
import ru.bogdanium.model.Review;

import java.util.List;

public class Sql2oReviewDao implements ReviewDao {

    private static final String SQL_ADD_REVIEW = "" +
            " INSERT INTO reviews(course_id, rating, comment)" +
            " VALUES (:courseId, :rating, :comment)";

    private static final String SQL_GET_ALL_REVIEWS = "SELECT * FROM reviews";

    private static final String SQL_GET_COURSE_BY_ID = "" +
            "SELECT * FROM reviews WHERE course_id = :course_id";

    private final Sql2o sql2o;

    public Sql2oReviewDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Review review) throws DaoException {

        try (Connection connection = sql2o.open()) {
            int key = (int) connection
                    .createQuery(SQL_ADD_REVIEW)
                    .bind(review)
                    .executeUpdate()
                    .getKey();

            review.setId(key);

        } catch (Sql2oException e) {
            throw new DaoException(e, "Problem adding review");
        }
    }

    @Override
    public List<Review> findAll() {

        try (Connection connection = sql2o.open()) {
            return connection
                    .createQuery(SQL_GET_ALL_REVIEWS)
                    .executeAndFetch(Review.class);
        }
    }

    @Override
    public List<Review> findByCourseId(int courseId) {
        try (Connection connection = sql2o.open()) {
            return connection
                    .createQuery(SQL_GET_COURSE_BY_ID)
                    .addColumnMapping("course_id", "courseId")
                    .addParameter("course_id", courseId)
                    .executeAndFetch(Review.class);
        }
    }
}
