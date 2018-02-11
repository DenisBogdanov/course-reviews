package ru.bogdanium.dao;

import ru.bogdanium.exception.DaoException;
import ru.bogdanium.model.Review;

import java.util.List;

public interface ReviewDao {
    void add(Review review) throws DaoException;

    List<Review> findAll();

    List<Review> findByCourseId(int courseId);
}
