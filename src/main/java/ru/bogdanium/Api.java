package ru.bogdanium;

import com.google.gson.Gson;
import org.sql2o.Sql2o;
import ru.bogdanium.dao.CourseDao;
import ru.bogdanium.dao.Sql2oCourseDao;
import ru.bogdanium.model.Course;

import static spark.Spark.*;

public class Api {
    public static void main(String[] args) {

        Sql2o sql2o = new Sql2o("jdbc:h2:~/reviews.db; INIT=RUNSCRIPT from 'classpath:db/init.sql'");
        CourseDao courseDao = new Sql2oCourseDao(sql2o);
        Gson gson = new Gson();

        post("/courses", "application/json", (req, res) -> {
            Course course = gson.fromJson(req.body(), Course.class);
            courseDao.add(course);
            res.status(201);
            return course;
        }, gson::toJson);

        get("/courses", "application/json", (req, res) -> courseDao.findAll(), gson::toJson);

        get("/courses/:id", "applications/json", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            return null;
        });

        after((req, res) -> {
            res.type("application/json");
        });
    }
}
