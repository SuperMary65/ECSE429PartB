Feature: Update a title of existent todo
  As a user, I want to update title of created todo
  so I can have information up-to-date

  Background: Given the todo manager service is running

    # NORMAL FLOW
    Scenario Outline: Update a title using POST without changing other fields
      Given a todo exists
      When I update the todo title to "<newTitle>" using "POST"
      Then the response status code is 200
      And todo has title "<newTitle>"
      And todo has doneStatus "true"
      And todo has description "in the gym"

      Examples:
        | newTitle     |
        | buy granola   |
        | study |

      # ALTERNATIVE FLOW
    Scenario Outline: Update a title using PUT, and other fields DOES change
      Given a todo exists
      When I update the todo title to "<newTitle>" using "PUT"
      Then the response status code is 200
      And todo has title "<newTitle>"
      And todo has doneStatus "false"
      And todo has no description

      Examples:
        | newTitle     |
        | buy granola   |
        | study |

      # ERROR FLOW
    Scenario Outline: Update a title of non-existent todo
      Given todo "<id>" does not exist
      When I update the todo "<id>" title to "<newTitle>" using "POST"
      Then the response status code is 404

      Examples:
        | id    | newTitle    |
        | 99999 | new title   |

