Feature: Gerenciar cadastro de salas

  Scenario: Secretaria inativa uma sala ativa
    Given uma sala ativa cadastrada com capacidade para trinta pessoas
    When a secretaria inativa a sala
    Then a sala passa a ter status inativo

  Scenario: Secretaria tenta reduzir capacidade da sala abaixo de turma vinculada
    Given uma sala ativa cadastrada com capacidade para trinta pessoas
    When a secretaria tenta reduzir a capacidade da sala para vinte pessoas havendo turma com trinta vagas
    Then o sistema rejeita a redução de capacidade da sala
