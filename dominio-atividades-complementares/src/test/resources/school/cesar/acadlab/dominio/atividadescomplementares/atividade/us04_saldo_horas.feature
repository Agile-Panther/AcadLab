Feature: Visualizar Saldo de Horas Complementares

  Scenario: Saldo considera apenas atividades deferidas por categoria
    Given o estudante 2 possui uma atividade DEFERIDA na categoria 1 com 40 horas aprovadas
    And o estudante 2 possui uma atividade PENDENTE na categoria 1 com 20 horas submetidas
    And o estudante 2 possui uma atividade INDEFERIDA na categoria 2 com 30 horas submetidas
    When consulto o saldo de horas do estudante 2
    Then o saldo da categoria 1 deve ser 40 horas
    And a categoria 2 não deve aparecer no saldo
