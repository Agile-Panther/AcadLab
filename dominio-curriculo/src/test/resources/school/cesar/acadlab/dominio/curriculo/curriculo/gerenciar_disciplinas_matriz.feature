#language: pt
Funcionalidade: Gerenciar disciplinas da matriz curricular

  Cenário: Adding a discipline to an inactive matrix
    Dado a curricular matrix in RASCUNHO status
    Quando the coordinator adds a discipline with id 5 to the matrix
    Então the matrix should contain the discipline with id 5

  Cenário: Adding a duplicate discipline is rejected
    Dado a curricular matrix in RASCUNHO status with discipline 5 already added
    Quando the coordinator tries to add discipline 5 again
    Então the system rejects the addition with message containing "RN-1"

  Cenário: Removing a discipline from an inactive matrix with no classes
    Dado a curricular matrix in RASCUNHO status with discipline 5 already added
    Quando the coordinator removes discipline 5 from the matrix
    Então the matrix should not contain the discipline with id 5

  Cenário: Removing a discipline linked to classes is rejected
    Dado a curricular matrix in RASCUNHO status with discipline 5 already added
    E discipline 5 is linked to existing classes
    Quando the coordinator tries to remove discipline 5
    Então the system rejects the removal with message containing "RN-9"
