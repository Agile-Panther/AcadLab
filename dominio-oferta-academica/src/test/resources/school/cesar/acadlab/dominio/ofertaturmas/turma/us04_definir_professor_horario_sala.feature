Feature: Definir professor, horario e sala da turma com verificacao de conflito

  Scenario: Coordenacao vincula professor sem conflito de horario
    Given uma turma planejada com horario segunda 08h as 10h no periodo 1
    And um professor sem turmas no mesmo periodo
    When a coordenacao adiciona o horario e vincula o professor a turma
    Then o professor e vinculado com sucesso

  Scenario: Coordenacao tenta vincular professor com conflito de horario
    Given uma turma planejada com horario segunda 08h as 10h no periodo 1
    And o professor ja tem turma com horario segunda 09h as 11h no mesmo periodo
    When a coordenacao tenta vincular o professor com conflito
    Then o sistema rejeita a vinculacao informando conflito de horario do professor

  Scenario: Coordenacao tenta vincular sala com conflito de horario
    Given uma turma planejada com horario segunda 08h as 10h no periodo 1
    And a sala ja esta alocada em turma com horario segunda 09h as 11h no mesmo periodo
    When a coordenacao tenta vincular a sala com conflito
    Then o sistema rejeita a vinculacao informando conflito de horario da sala
