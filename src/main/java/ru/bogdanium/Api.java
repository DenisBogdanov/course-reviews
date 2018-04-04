package ru.bogdanium;

import com.google.gson.Gson;
import org.sql2o.Sql2o;
import ru.bogdanium.dao.CourseDao;
import ru.bogdanium.dao.ReviewDao;
import ru.bogdanium.dao.Sql2oCourseDao;
import ru.bogdanium.dao.Sql2oReviewDao;
import ru.bogdanium.exception.ApiError;
import ru.bogdanium.exception.DaoException;
import ru.bogdanium.model.Course;
import ru.bogdanium.model.Review;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Api {
    public static void main(String[] args) {

        String datasourse = "jdbc:h2:~\\Dropbox\\Projects\\sparkjava\\course-reviews\\data\\reviews.db";

        if (args.length > 0) {
            if (args.length != 2) {
                System.out.println("java Api <port> <datasourse>");
                System.exit(0);
            }
            port(Integer.parseInt(args[0]));
            datasourse = args[1];
        }

        Sql2o sql2o = new Sql2o(datasourse +
                ";INIT=RUNSCRIPT from 'classpath:db/init.sql'", "", "");

        CourseDao courseDao = new Sql2oCourseDao(sql2o);
        ReviewDao reviewDao = new Sql2oReviewDao(sql2o);

        Gson gson = new Gson();

        post("/courses", "application/json", (req, res) -> {
            Course course = gson.fromJson(req.body(), Course.class);
            courseDao.add(course);
            res.status(201);
            return course;
        }, gson::toJson);

        get("/courses", "application/json",
                (req, res) -> courseDao.findAll(), gson::toJson);

        get("/courses/:id", "applications/json", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            Course course = courseDao.findById(id);
            if (course == null) {
                throw new ApiError(404, "Could not find course");
            }
            return course;
        }, gson::toJson);

        post("/courses/:courseId/reviews", "application/json", (req, res) -> {
            int courseId = Integer.parseInt(req.params("courseId"));
            Review review = gson.fromJson(req.body(), Review.class);
            review.setCourseId(courseId);
            try {
                reviewDao.add(review);
            } catch (DaoException e) {
                throw new ApiError(500, e.getMessage());
            }
            res.status(201);
            return review;
        }, gson::toJson);

        get("/courses/:courseId/reviews", "application/json", (req, res) -> {
            int courseId = Integer.parseInt(req.params("courseId"));
            return reviewDao.findByCourseId(courseId);
        }, gson::toJson);

        exception(ApiError.class, (e, req, res) -> {
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", e.getStatus());
            jsonMap.put("errorMessage", e.getMessage());
            res.type("application/json");
            res.status(e.getStatus());
            res.body(gson.toJson(jsonMap));
        });

        after((req, res) -> {
            res.type("application/json");
        });
    }
}
