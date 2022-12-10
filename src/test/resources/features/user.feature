@smoke
Feature: User Verification

  Scenario: verify information about logged user
    #Given I logged Bookit api using "blyst6@si.edu" and "barbabaslyst"
    Given I logged Bookit api using "sbirdbj@fc2.com" and "asenorval"
    When I get the current user information from api
    Then status code should be 200

#@wip
  Scenario Outline: verify information about logged users
    Given I logged Bookit api using "<email>" and "<password>"
    When I get the current user information from api
    Then status code should be 200
    Examples:
      | email           | password     |
      | sbirdbj@fc2.com | asenorval    |
      | blyst6@si.edu   | barbabaslyst |

#@wip
  Scenario: verify information about logged user from api and database
    Given I logged Bookit api using "sbirdbj@fc2.com" and "asenorval"
    When I get the current user information from api
    Then the information about current user from api and database should match


  @db @ui
  Scenario: three point verification (UI,API,Database)
    Given user logs in using "blyst6@si.edu" "barbabaslyst"
    #Given user logs in using "wcanadinea@ihg.com" "waverleycanadine"
    And  user is on the my self page
    Given I logged Bookit api using "blyst6@si.edu" and "barbabaslyst"
    When I get the current user information from api
    Then UI,API and Database user information must be match

  @db @ui
  Scenario Outline: three point verification (UI,API,Database) DDT
    Given user logs in using "<email>" "<password>"
    And  user is on the my self page
    Given I logged Bookit api using "<email>" and "<password>"
    When I get the current user information from api
    Then UI,API and Database user information must be match

    Examples:
      | email           | password     |
      | blyst6@si.edu   | barbabaslyst |
      | sbirdbj@fc2.com | asenorval    |


  Scenario: create a teacher and and verify status code 201
    When I send POST request to "/api/students/student" endpoint with following information
      | first-name      | harold              |
      | last-name       | finch               |
      | email           | harom1@gmail.com |
      | password        | abc                 |
      | role            | student-team-leader |
      | campus-location | VA                  |
      | batch-number    | 8                   |
      | team-name       | Nukes               |
    Then status code should be 201
    And I delete previously added student

#    get name. role, batch number, team name, campus from api for one student
#    it will be multiple api request
#    responses return batch name with students info
#    first make sure your student is insdie the response then get those info
#    prepare one list of info about studnet and compare witk ui
#


