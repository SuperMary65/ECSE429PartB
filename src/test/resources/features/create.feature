Feature: create a todo
  As a user, I want to create a todo so that I can remember a task.

  Background: Given the todo manager service is running

    # NORMAL FLOW
  Scenario Outline: Create a todo with all fields (normal flow)
    When I create a todo with:
      | title       | <title> |
      | doneStatus  | <done>  |
      | description | <desc>  |
    Then the response status code is 201
    And the created todo can be retrieved

    Examples:
      | title      | done  | desc          |
      | workout    | false  | in the gym   |
      | buy granola | true  | with discount |

    # ALTERNATIVE FLOW
  Scenario Outline: Create a todo with only title (alternative flow)
    When I create a todo with:
      | title       | <title> |
    Then the response status code is 201
    And the created todo can be retrieved

    Examples:
      | title      |
      | workout    |
      | buy granola |

    # ERROR FLOW
  Scenario Outline: Create a todo without a title
    When I create a todo with:
      | doneStatus  | <done> |
      | description | <desc> |
    Then the response status code is 400

    Examples:
      | done  | desc         |
      | false | test todo    |
      | true  | no title todo |

