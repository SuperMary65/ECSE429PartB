package steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TodoSteps {

    private final TodoApi api = new TodoApi();

    private Response response;
    private String createdTodoId;
    private String createdTodoTitle;
    private final List<String> createdTodoIds = new ArrayList<>();

    private String createdProjectId;
    private final List<String> createdProjectIds = new ArrayList<>();

    // START AND END----------------------------------------
    @Before
    public void setUp(){
        response = null;
        createdTodoId = null;
        createdTodoIds.clear();
        createdProjectId = null;
        createdProjectIds.clear();
    }

    @After
    public void cleanUp(){
        for (String id : createdTodoIds) {
            Response existing = api.getTodo(id);
            if (existing.statusCode() == 200) { //todo still exists, so delete it
                Response deleted = api.deleteTodo(id);
                assertEquals("Could not clean up todo " + id,
                        200, deleted.statusCode());
            } else if (existing.statusCode() != 404) { //it has been deleted, do nothing
                fail("Could not check todo " + id + " before cleanup. " +
                        "Status: " + existing.statusCode());
            }
        }
        // delete projects created during this scenario
        for (String id : createdProjectIds) {
            Response deleted = api.deleteProject(id);
            assertTrue("Could not clean up project " + id,
                    deleted.statusCode() == 200 || deleted.statusCode() == 404);
        }
    }

    // GENERAL STEPS----------------------------------------

    @Given("the todo manager service is running")
    public void serviceIsRunning() {
        response = api.getAllTodos();
        assertEquals(200, response.statusCode());
    }

    @Then("the response status code is {int}")
    public void responseStatusIs(int expectedStatus) {
        assertNotNull("response is null", response);
        assertEquals(expectedStatus, response.statusCode());
    }

    // CREATION STEPS----------------------------------------

    @When("I create a todo with:")
    public void createTodo(DataTable table){
        Map<String, String> fields = table.asMap(String.class, String.class);

        String title = fields.get("title");
        String doneValue = fields.get("doneStatus");
        String description = fields.get("description");

        Boolean doneStatus = doneValue == null
                ? null //if true
                : Boolean.parseBoolean(doneValue); //if false, make string into boolean

        response = api.createTodo(title, doneStatus, description);

        // save the ID if creation succeeded, so we can retrieve and delete it
        if (response.statusCode() == 201){
            createdTodoId = response.jsonPath().getString("id");
            createdTodoIds.add(createdTodoId);
            createdTodoTitle = title;
        }
    }

    @Then("the created todo can be retrieved")
    public void todoCanBeRetrieved() {
        assertNotNull("No todo ID was saved", createdTodoId);
        Response retrieved = api.getTodo(createdTodoId);

        assertEquals(200, retrieved.statusCode());
        // verify that the returned todo has the ID we requested
        assertEquals(createdTodoId,
                retrieved.jsonPath().getString("todos[0].id"));
    }

    // UPDATE STEPS---------------------------------------------

    @Given("a todo exists")
    public void todoExists() {
        response = api.createTodo("workout", true, "in the gym");

        assertEquals(201, response.statusCode());
        createdTodoId = response.jsonPath().getString("id");

        assertNotNull("Created todo has no ID", createdTodoId);
        createdTodoIds.add(createdTodoId);
    }

    @When("I update the todo title to {string} using {string}")
    public void updateTitle(String newTitle, String function){
        response = privateUpdateTitle(createdTodoId, newTitle, function);
    }

    @When("I update the todo {string} title to {string} using {string}")
    public void updateTitle(String id, String newTitle, String function){
        response = privateUpdateTitle(id, newTitle, function);
    }

    private Response privateUpdateTitle(String id, String newTitle, String function){
        Map<String, Object> changes = new HashMap<>();
        changes.put("title", newTitle);

        if (function.equalsIgnoreCase("POST")){
            return api.postUpdateTodo(id, changes);
        }
        if (function.equalsIgnoreCase("PUT")){
            return api.putUpdateTodo(id, changes);
        }
        throw new IllegalArgumentException("Not supported function" + function);
    }

    @Then("todo has title {string}")
    public void checkTitle(String expectedTitle){
        Response retrieved = getCurrentTodo();

        assertEquals(expectedTitle, retrieved.jsonPath().getString("todos[0].title"));
    }

    @Then("todo has doneStatus {string}")
    public void checkDoneStatus(String expectedDoneStatus){
        Response retrieved = getCurrentTodo();

        assertEquals(expectedDoneStatus, retrieved.jsonPath().getString("todos[0].doneStatus"));
    }

    @Then("todo has description {string}")
    public void checkTodoDescription(String expectedDescription) {

        Response retrieved = getCurrentTodo();

        assertEquals(
                expectedDescription,
                retrieved.jsonPath().getString("todos[0].description")
        );
    }

    private Response getCurrentTodo(){
        Response retrieved = api.getTodo(createdTodoId);
        assertEquals(200, retrieved.statusCode());
        return retrieved;
    }

    @Then("todo has no description")
    public void checkTodoHasNoDescription() {

        Response retrieved = getCurrentTodo();

        Object description =
                retrieved.jsonPath().get("todos[0].description");

        assertTrue(
                "Expected description to be empty, but was: " + description,
                description == null
                        || description.toString().isBlank()
                        || description.toString().equalsIgnoreCase("null")
        );
    }

    // DELETION STEPS ------------------------------------------
    @When("I delete the todo {string}")
    public void deleteTodoWithId(String id) {
        response = api.deleteTodo(id);
    }

    @When("I delete the todo")
    public void deleteTodo() {
        response = api.deleteTodo(createdTodoId);
    }

    @And("the todo cannot be retrieved")
    public void todoCannotBeRetrieved() {
        response = api.getTodo(createdTodoId);
        assertEquals(404, response.statusCode());
    }
    // create a todo with a specific title for deletion scenarios
    @Given("a todo exists with title {string}")
    public void todoExistsWithTitle(String title) {

        createdTodoTitle = title;

        response = api.createTodo(title, false, "created for deletion test");

        assertEquals(201, response.statusCode());

        createdTodoId = response.jsonPath().getString("id");

        assertNotNull("Created todo has no ID", createdTodoId);

        createdTodoIds.add(createdTodoId);
    }

    @When("I find the todo by title and delete it")
    public void findTodoByTitleAndDelete() {
        Response found = api.getTodosByTitle(createdTodoTitle);
        assertEquals(200, found.statusCode());

        createdTodoId = found.jsonPath().getString("todos[0].id");
        assertNotNull("Todo was not found", createdTodoId);

        response = api.deleteTodo(createdTodoId);
    }

    // verify that an ID is really nonexistent before testing an error flow
    @Given("todo {string} does not exist")
    public void todoDoesNotExist(String id) {
        Response retrieved = api.getTodo(id);
        assertEquals(404, retrieved.statusCode());
    }

    // CATEGORY STEPS ----------------------------------------

    @When("I assign the todo to category {string}")
    public void assignTodoToCategory(String categoryId) {
        response = api.assignCategory(createdTodoId, categoryId);
    }

    @When("I assign todo {string} to category {string}")
    public void assignCategoryToTodo(String todoId, String categoryId) {
        response = api.assignCategory(todoId, categoryId);
    }

    @Then("the todo is linked to category {string}")
    public void checkTodoCategory(String categoryId) {
        Response retrieved = api.getTodoCategories(createdTodoId);

        assertEquals(200, retrieved.statusCode());

        List<String> categoryIds =
                retrieved.jsonPath().getList("categories.id", String.class);

        assertTrue(categoryIds.contains(categoryId));
    }

    @Then("the todo is linked to categories {string} and {string}")
    public void checkTodoCategories(String firstId, String secondId) {
        Response retrieved = api.getTodoCategories(createdTodoId);

        assertEquals(200, retrieved.statusCode());

        List<String> categoryIds =
                retrieved.jsonPath().getList("categories.id", String.class);

        assertTrue(categoryIds.contains(firstId));
        assertTrue(categoryIds.contains(secondId));
    }

    // PROJECT STEPS -----------------------------------------

    @When("I assign the todo to project {string}")
    public void assignTodoToProject(String projectId) {
        response = api.assignProject(createdTodoId, projectId);
    }

    @When("I assign todo {string} to project {string}")
    public void assignProjectToTodo(String todoId, String projectId) {
        response = api.assignProject(todoId, projectId);
    }

    @Then("the todo is linked to project {string}")
    public void checkTodoProject(String projectId) {
        Response retrieved = api.getTodoProjects(createdTodoId);

        assertEquals(200, retrieved.statusCode());

        List<String> projectIds =
                retrieved.jsonPath().getList("projects.id", String.class);

        assertTrue(projectIds.contains(projectId));
    }

    @Then("the todo is linked to projects {string} and {string}")
    public void checkTodoProjects(String firstId, String secondId) {
        Response retrieved = api.getTodoProjects(createdTodoId);

        assertEquals(200, retrieved.statusCode());

        List<String> projectIds =
                retrieved.jsonPath().getList("projects.id", String.class);

        assertTrue(projectIds.contains(firstId));
        assertTrue(projectIds.contains(secondId));
    }

    @Given("a new project exists")
    public void newProjectExists() {
        Response projectResponse = api.createProject("Test project");
        assertEquals(201, projectResponse.statusCode());

        createdProjectId = projectResponse.jsonPath().getString("id");
        assertNotNull("Created project has no ID", createdProjectId);
        createdProjectIds.add(createdProjectId);
    }

    @When("I assign the todo to the new project")
    public void assignTodoToNewProject() {
        response = api.assignProject(createdTodoId, createdProjectId);
    }

    @Then("the todo is linked to projects {string} and the new project")
    public void checkTodoProjects(String existingProjectId) {
        Response retrieved = api.getTodoProjects(createdTodoId);
        assertEquals(200, retrieved.statusCode());

        List<String> projectIds =  retrieved.jsonPath().getList("projects.id", String.class);

        assertTrue(projectIds.contains(existingProjectId));
        assertTrue(projectIds.contains(createdProjectId));
    }
}
