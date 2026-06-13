Feature: Cancelar período letivo

  Scenario: Secretaria cancela período letivo não iniciado sem matrículas
    Given um período letivo cadastrado passível de cancelamento
    And sem matrículas confirmadas no período
    When a secretaria cancela o período letivo
    Then o período letivo deve ter status cancelado

  Scenario: Sistema rejeita cancelamento com matrículas confirmadas
    Given um período letivo cadastrado passível de cancelamento
    And com matrículas confirmadas que impedem o cancelamento
    When a secretaria tenta cancelar o período letivo
    Then o sistema rejeita o cancelamento informando matrículas confirmadas

  Scenario: Sistema rejeita cancelamento de período já encerrado
    Given um período letivo já encerrado para tentativa de cancelamento
    When a secretaria tenta cancelar o período letivo
    Then o sistema rejeita o cancelamento informando status inválido
