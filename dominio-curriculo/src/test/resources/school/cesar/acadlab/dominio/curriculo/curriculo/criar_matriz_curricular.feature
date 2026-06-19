#language: pt
Funcionalidade: Criar matriz curricular

  Cenário: Coordinator creates a curricular matrix with sufficient workload
    Dado a course with id 10
    E a new curricular matrix named "Engenharia de Software" with minimum workload 2400 and minimum credits 160
    E a discipline with id 1 is added with workload 2400 and credits 160
    Quando the coordinator activates the matrix
    Então the matrix status should be ATIVA

  Cenário: Activating matrix with insufficient workload is rejected
    Dado a course with id 10
    E a new curricular matrix named "Engenharia de Software" with minimum workload 2400 and minimum credits 160
    E a discipline with id 1 is added with workload 60 and credits 4
    Quando the coordinator tries to activate the matrix
    Então the system rejects the activation with message containing "RN-2"
