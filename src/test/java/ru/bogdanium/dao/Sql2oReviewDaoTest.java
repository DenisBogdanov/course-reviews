package ru.bogdanium.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import ru.bogdanium.exception.DaoException;
import ru.bogdanium.model.Course;
import ru.bogdanium.model.Review;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class Sql2oReviewDaoTest {

    private Sql2oCourseDao courseDao;
    private Sql2oReviewDao reviewDao;
    private Connection connection;
    private Course course;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        courseDao = new Sql2oCourseDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);

        // Keep connection open through entire test
        connection = sql2o.open();

        course = new Course("Test", "TestUrl");
        courseDao.add(course);
    }

    @Test
    public void addingReviewSetsId() throws DaoException {
        Review review = new Review(course.getId(), 5, "Good");
        int originalReviewId = review.getId();
        reviewDao.add(review);

        assertNotEquals(originalReviewId, review.getId());
    }

    @Test
    public void multipleReviewsAreFoundWhenTheyExistForACourse() throws DaoException {
        Review review1 = new Review(course.getId(), 5, "Good");
        Review review2 = new Review(course.getId(), 4, "Great");
        Review review3 = new Review(course.getId(), 42, "Bad");

        reviewDao.add(review1);
        reviewDao.add(review2);
        reviewDao.add(review3);

        List<Review> reviews = reviewDao.findByCourseId(course.getId());

        assertEquals(3, reviews.size());
    }

    @Test(expected = DaoException.class)
    public void addingAReviewToANonExistingCourseFails() throws DaoException {
        reviewDao.add(new Review(42, 5, "Failed review"));
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }
}