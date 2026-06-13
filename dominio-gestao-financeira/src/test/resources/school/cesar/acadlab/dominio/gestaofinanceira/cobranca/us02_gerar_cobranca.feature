Feature: Gerar Cobrança e Nova Versão

  Scenario: Gerar cobrança para estudante com matrícula confirmada
    Given o estudante 3 possui matrícula confirmada no período letivo 1
    When gero uma cobrança para o estudante 3 no contrato 2 com valor 1500.00
    Then a cobrança deve ser gerada com status ABERTA

  Scenario: Rejeitar geração de cobrança sem matrícula confirmada
    Given o estudante 4 não possui matrícula confirmada no período letivo 1
    When tento gerar uma cobrança para o estudante 4 no contrato 3 com valor 1500.00
    Then deve ser lançada uma exceção de matrícula não confirmada

  Scenario: Gerar nova versão de cobrança existente
    Given o estudante 5 possui matrícula confirmada no período letivo 1
    And uma cobrança foi gerada para o estudante 5 no contrato 4
    When gero nova versão da cobrança com motivo "Reajuste anual" e valor 1600.00
    Then a cobrança deve estar na versão 2 com valor 1600.00
