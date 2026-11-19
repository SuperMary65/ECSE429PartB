Feature: Assign a todo to a project

  As a user, I want to assign a todo to a project
  so that I can organize related tasks together.

  Background:
    Given the todo manager service is running

  # NORMAL FLOW
  Scenario Outline: Assign a todo to an existing project
    Given a todo exists
    When I assign the todo to project "<projectId>"
    Then the response status code is 201
    And the todo is linked to project "<projectId>"

    Examples:
      | projectId |
      | 1         |
  # ALTERNATIVE FLOW
  Scenario Outline: Assign multiple projects to the same todo
    Given a todo exists
    And a new project exists
    When I assign the todo to project "<existingProjectId>"
    Then the response status code is 201
    When I assign the todo to the new project
    Then the response status code is 201
    And the todo is linked to projects "<existingProjectId>" and the new project

    Examples:
      | existingProjectId |
      | 1                 |

  # ERROR FLOW
  Scenario Outline: Assign a project to a nonexistent todo
    Given todo "<todoId>" does not exist
    When I assign todo "<todoId>" to project "<projectId>"
    Then the response status code is 404

    Examples:
      | todoId | projectId |
      | 99999  | 1         |