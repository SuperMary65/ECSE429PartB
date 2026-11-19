# ECSE 429 Part B Story Testing of REST API

## Project description

This project implements acceptance tests for the Todo Manager REST API using Cucumber, Gherkin and REST Assured.

The test suite covers five user stories:

1. Create a todo.
2. Update a todo's title.
3. Delete a todo.
4. Assign a todo to a category.
5. Assign a todo to a project.

Each story contains normal, alternate and error flows.

## Technologies

- Java
- Maven
- Cucumber
- Gherkin
- REST Assured
- JUnit Platform

## Project structure

- `TodoApi.java` – reusable REST API requests.
- `TodoSteps.java` – Cucumber step definitions.
- `RunCucumberTest.java` – Cucumber test runner.
- `features/` – Gherkin acceptance tests.
- `junit-platform.properties` – random scenario execution settings.

## How to run

1. Download the Todo Manager REST API JAR from the course materials.
2. Start the application:

   `java -jar runTodoManagerRestAPI-1.5.5.jar`

3. Run the tests using Maven:

   `mvn -Dtest=RunCucumberTest test`

The application must be running at `http://localhost:4567`.

## Notes

The project uses `/todos/:id/tasksof` for todo-project relationships because this endpoint works in the application, while the documented `/todos/:id/task-of` returns 404.

All test data created during scenarios is cleaned up after execution.

## Related work

Part A repository:  
https://github.com/SuperMary65/ESCE429Ass2PartA
