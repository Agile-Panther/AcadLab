Feature: Gerenciar cadastro de professores

  Scenario: Secretaria inativa um professor ativo
    Given um professor ativo cadastrado
    When a secretaria inativa o professor
    Then o professor passa a ter status inativo

  Scenario: Coordenação tenta vincular professor inativo a uma turma
    Given um professor ativo cadastrado
    When a coordenação tenta vincular o professor inativo a uma turma
    Then o sistema rejeita a vinculação do professor inativo
