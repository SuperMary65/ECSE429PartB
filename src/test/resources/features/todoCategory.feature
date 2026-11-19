Feature: Assign a todo to a category

  As a user, I want to assign a todo to a category
  so that I can organize my tasks.

  Background:
    Given the todo manager service is running

  # NORMAL FLOW
  Scenario Outline: Assign a todo to an existing category
    Given a todo exists
    When I assign the todo to category "<categoryId>"
    Then the response status code is 201
    And the todo is linked to category "<categoryId>"

    Examples:
      | categoryId |
      | 1          |

  # ALTERNATIVE FLOW
  Scenario Outline: Assign multiple categories to the same todo
    Given a todo exists
    When I assign the todo to category "<firstId>"
    And I assign the todo to category "<secondId>"
    Then the response status code is 201
    And the todo is linked to categories "<firstId>" and "<secondId>"

    Examples:
      | firstId | secondId |
      | 1       | 2        |
  # ERROR FLOW
  Scenario Outline: Assign a category to a nonexistent todo
    Given todo "<todoId>" does not exist
    When I assign todo "<todoId>" to category "<categoryId>"
    Then the response status code is 404

    Examples:
      | todoId | categoryId |
      | 99999  | 1          |