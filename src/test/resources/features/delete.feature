Feature: delete existing todo
  As a user, I want to delete a todo
  that is no longer needed

  Background: Given the todo manager service is running

   # NORMAL FLOW
  Scenario Outline: Delete an existing todo using its ID
    Given a todo exists with title "<title>"
    When I delete the todo
    Then the response status code is 200
    And the todo cannot be retrieved

    Examples:
      | title          |
      | completed workout   |
      | already bought granola   |

    # ALTERNATIVE FLOW
  Scenario Outline: Find a todo by title and delete it
    Given a todo exists with title "<title>"
    When I find the todo by title and delete it
    Then the response status code is 200
    And the todo cannot be retrieved

    Examples:
      | title          |
      | completed workout   |
      | already bought granola   |

    #ERROR FLOW
  Scenario Outline: Delete a nonexistent todo
    Given todo "<id>" does not exist
    When I delete the todo "<id>"
    Then the response status code is 404

    Examples:
      | id    |
      | 99999 |