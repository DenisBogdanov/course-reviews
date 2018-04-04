package ru.bogdanium;

import com.google.gson.Gson;
import org.junit.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import ru.bogdanium.dao.Sql2oCourseDao;
import ru.bogdanium.model.Course;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ApiTest {

    private static final String TEST_PORT = "4568";
    private static final String TEST_DATASOURSE = "jdbc:h2:mem:testing";

    private Connection connection;
    private ApiClient client;
    private Gson gson;
    private Sql2oCourseDao courseDao;

    @BeforeClass
    public static void startServer() {
        String[] args = {TEST_PORT, TEST_DATASOURSE};
        Api.main(args);
    }

    @Before
    public void setUp() throws Exception {
        Sql2o sql2o = new Sql2o(TEST_DATASOURSE + ";INIT=RUNSCRIPT from 'classpath:db/init.sql'", "", "");
        courseDao = new Sql2oCourseDao(sql2o);
        connection = sql2o.open();
        client = new ApiClient("http://localhost:" + TEST_PORT);
        gson = new Gson();
    }

    @Test
    public void addingCoursesReturnsCreatedStatus() throws Exception {
        Map<String, String> values = new HashMap<>();
        values.put("name", "Test");
        values.put("url", "http://test.com");

        ApiResponse res = client.request("POST", "/courses", gson.toJson(values));

        assertEquals(201, res.getStatus());
    }

    @Test
    public void coursesCanBeAccessedById() throws Exception {
        Course course = newTestCourse();
        courseDao.add(course);

        ApiResponse res = client.request("GET", "/courses/" + course.getId());
        Course retrieved = gson.fromJson(res.getBody(), Course.class);

        assertEquals(course, retrieved);
    }

    @Test
    public void missingCoursesReturnNotFoundStatus() {
        ApiResponse res = client.request("GET", "/courses/42");

        assertEquals(404, res.getStatus());
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @AfterClass
    public static void stopServer() {
        Spark.stop();
    }

    private Course newTestCourse() {
        return new Course("Test", "http://test.com");
    }
}