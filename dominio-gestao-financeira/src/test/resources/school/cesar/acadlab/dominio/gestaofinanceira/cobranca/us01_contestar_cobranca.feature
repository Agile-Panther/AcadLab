Feature: Contestar Cobrança

  Scenario: Estudante contesta sua própria cobrança
    Given uma cobrança aberta para o estudante 1 no contrato 1
    When o estudante 1 contesta a cobrança com justificativa "Valor diverge do contrato"
    Then a cobrança deve ter status CONTESTADA

  Scenario: Rejeitar contestação de cobrança de outro estudante
    Given uma cobrança aberta para o estudante 1 no contrato 1
    When o estudante 2 tenta contestar a cobrança do estudante 1
    Then deve ser lançada uma exceção de contestação indevida

  Scenario: Rejeitar segunda contestação pendente
    Given uma cobrança aberta para o estudante 1 no contrato 1
    And o estudante 1 já contestou a cobrança anteriormente
    When o estudante 1 tenta contestar a cobrança novamente
    Then deve ser lançada uma exceção de contestação duplicada
