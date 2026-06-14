Feature: Gerenciar status da matriz curricular

  Scenario: Activating a matrix when no active matrix exists for the course
    Given a curricular matrix with sufficient workload for course 20
    When the coordinator activates the matrix
    Then the matrix status should be ATIVA

  Scenario: Activating a matrix when one already exists is rejected
    Given a curricular matrix already active for course 20
    And a second curricular matrix with sufficient workload for course 20
    When the coordinator tries to activate the second matrix
    Then the system rejects the activation with message containing "RN-5"
