#language: pt
Funcionalidade: Configurar pré-requisitos e correquisitos

  Cenário: Adding a prerequisite between disciplines works
    Dado a curricular matrix with two disciplines with ids 1 and 2
    Quando the coordinator adds discipline 1 as a prerequisite for discipline 2
    Então discipline 2 should have discipline 1 as a prerequisite

  Cenário: Adding a cyclic prerequisite is rejected
    Dado a curricular matrix with two disciplines with ids 1 and 2
    E discipline 1 is already a prerequisite for discipline 2
    Quando the coordinator tries to add discipline 2 as a prerequisite for discipline 1
    Então the system rejects the prerequisite with message containing "RN-3"

  Cenário: Adding a corequisite from same matrix works
    Dado a curricular matrix with two disciplines with ids 1 and 2
    Quando the coordinator adds discipline 2 as a corequisite for discipline 1
    Então discipline 1 should have discipline 2 as a corequisite

  Cenário: Adding a corequisite from outside the matrix is rejected
    Dado a curricular matrix with one discipline with id 1
    Quando the coordinator tries to add discipline 99 as a corequisite for discipline 1
    Então the system rejects the corequisite with message containing "RN-4"
