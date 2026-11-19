package steps;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class TodoApi {
    private static final String BASE_URI = "http://localhost:4567";

    // common request configuration
    private RequestSpecification request(){
        return given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }
    public Response createTodo(String title, Boolean doneStatus, String description) {
        Map<String, Object> body = new HashMap<>();

        if (title != null) {
            body.put("title", title);
        }

        if (doneStatus != null)
            body.put("doneStatus", doneStatus);

        if (description != null)
            body.put("description", description);

        return request()
                .body(body)
                .when().post("/todos");
    }

    public Response getTodo(String id) {
        return request()
                .when().get("/todos/" + id);
    }

    public Response getAllTodos() {
        return request()
                .when().get("/todos");
    }

    public Response deleteTodo(String id) {
        return given()
                .baseUri(BASE_URI)
                .when().delete("/todos/" + id);
    }

    public Response getTodosByTitle(String title) {
        return request()
                .queryParam("title", title)
                .when().get("/todos");
    }

    // post changes only what we want
    public Response postUpdateTodo(String id, Map<String, Object> changes){
        return request()
                .body(changes)
                .when().post("/todos/" + id);
    }

    // put require title
    // if put changes todo, other parts are renewed
    public Response putUpdateTodo(String id, Map<String, Object> changes){
        return request()
                .body(changes)
                .when().put("/todos/" + id);
    }

    // categories
    public Response assignCategory(String todoId, String categoryId){
        return request()
                .body(Map.of("id", categoryId))
                .when().post("/todos/" + todoId + "/categories");
    }

    public Response getTodoCategories(String todoId) {
        return request()
                .when().get("/todos/" + todoId + "/categories");
    }

    // projects
    public Response assignProject(String todoId, String projectId){
        return request()
                .body(Map.of("id", projectId))
                .when().post("/todos/" + todoId + "/tasksof");
    }

    public Response getTodoProjects(String todoId) {
        return request()
                .when().get("/todos/" + todoId + "/tasksof");
    }

    public Response createProject(String title) {
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);

        return request()
                .body(body)
                .when().post("/projects");
    }

    public Response deleteProject(String id) {
        return request()
                .when().delete("/projects/" + id);
    }

}

