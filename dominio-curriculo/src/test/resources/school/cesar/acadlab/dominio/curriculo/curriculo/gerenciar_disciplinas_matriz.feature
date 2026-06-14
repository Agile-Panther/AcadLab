Feature: Gerenciar disciplinas da matriz curricular

  Scenario: Adding a discipline to an inactive matrix
    Given a curricular matrix in RASCUNHO status
    When the coordinator adds a discipline with id 5 to the matrix
    Then the matrix should contain the discipline with id 5

  Scenario: Adding a duplicate discipline is rejected
    Given a curricular matrix in RASCUNHO status with discipline 5 already added
    When the coordinator tries to add discipline 5 again
    Then the system rejects the addition with message containing "RN-1"

  Scenario: Removing a discipline from an inactive matrix with no classes
    Given a curricular matrix in RASCUNHO status with discipline 5 already added
    When the coordinator removes discipline 5 from the matrix
    Then the matrix should not contain the discipline with id 5

  Scenario: Removing a discipline linked to classes is rejected
    Given a curricular matrix in RASCUNHO status with discipline 5 already added
    And discipline 5 is linked to existing classes
    When the coordinator tries to remove discipline 5
    Then the system rejects the removal with message containing "RN-9"
