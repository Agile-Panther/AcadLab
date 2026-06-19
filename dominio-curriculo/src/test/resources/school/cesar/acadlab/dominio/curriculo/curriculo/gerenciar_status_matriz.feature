#language: pt
Funcionalidade: Gerenciar status da matriz curricular

  Cenário: Activating a matrix when no active matrix exists for the course
    Dado a curricular matrix with sufficient workload for course 20
    Quando the coordinator activates the matrix
    Então the matrix status should be ATIVA

  Cenário: Activating a matrix when one already exists is rejected
    Dado a curricular matrix already active for course 20
    E a second curricular matrix with sufficient workload for course 20
    Quando the coordinator tries to activate the second matrix
    Então the system rejects the activation with message containing "RN-5"
