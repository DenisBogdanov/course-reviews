package ru.bogdanium;

import com.google.gson.Gson;
import org.sql2o.Sql2o;
import ru.bogdanium.dao.CourseDao;
import ru.bogdanium.dao.Sql2oCourseDao;
import ru.bogdanium.exception.ApiError;
import ru.bogdanium.model.Course;

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
