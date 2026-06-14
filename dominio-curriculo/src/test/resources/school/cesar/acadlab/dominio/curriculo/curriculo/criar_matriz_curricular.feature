Feature: Criar matriz curricular

  Scenario: Coordinator creates a curricular matrix with sufficient workload
    Given a course with id 10
    And a new curricular matrix named "Engenharia de Software" with minimum workload 2400 and minimum credits 160
    And a discipline with id 1 is added with workload 2400 and credits 160
    When the coordinator activates the matrix
    Then the matrix status should be ATIVA

  Scenario: Activating matrix with insufficient workload is rejected
    Given a course with id 10
    And a new curricular matrix named "Engenharia de Software" with minimum workload 2400 and minimum credits 160
    And a discipline with id 1 is added with workload 60 and credits 4
    When the coordinator tries to activate the matrix
    Then the system rejects the activation with message containing "RN-2"
