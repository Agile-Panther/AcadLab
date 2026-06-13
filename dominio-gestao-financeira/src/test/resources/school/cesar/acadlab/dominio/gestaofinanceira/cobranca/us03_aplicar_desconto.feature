Feature: Aplicar Desconto em Cobrança

  Scenario: Aplicar desconto com autorização válida
    Given uma cobrança aberta de 1500.00 para o estudante 10
    And a autorização "AUTH-VALID-001" é válida
    When aplico um desconto de 10 por cento com autorização "AUTH-VALID-001"
    Then o valor atual da cobrança deve ser 1350.00

  Scenario: Rejeitar desconto com autorização inválida
    Given uma cobrança aberta de 1500.00 para o estudante 11
    When tento aplicar um desconto de 10 por cento com autorização "AUTH-INVALIDA"
    Then deve ser lançada uma exceção de autorização inválida
