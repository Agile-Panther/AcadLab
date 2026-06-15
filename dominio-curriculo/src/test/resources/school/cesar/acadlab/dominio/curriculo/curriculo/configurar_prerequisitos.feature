Feature: Configurar pré-requisitos e correquisitos

  Scenario: Adding a prerequisite between disciplines works
    Given a curricular matrix with two disciplines with ids 1 and 2
    When the coordinator adds discipline 1 as a prerequisite for discipline 2
    Then discipline 2 should have discipline 1 as a prerequisite

  Scenario: Adding a cyclic prerequisite is rejected
    Given a curricular matrix with two disciplines with ids 1 and 2
    And discipline 1 is already a prerequisite for discipline 2
    When the coordinator tries to add discipline 2 as a prerequisite for discipline 1
    Then the system rejects the prerequisite with message containing "RN-3"

  Scenario: Adding a corequisite from same matrix works
    Given a curricular matrix with two disciplines with ids 1 and 2
    When the coordinator adds discipline 2 as a corequisite for discipline 1
    Then discipline 1 should have discipline 2 as a corequisite

  Scenario: Adding a corequisite from outside the matrix is rejected
    Given a curricular matrix with one discipline with id 1
    When the coordinator tries to add discipline 99 as a corequisite for discipline 1
    Then the system rejects the corequisite with message containing "RN-4"
