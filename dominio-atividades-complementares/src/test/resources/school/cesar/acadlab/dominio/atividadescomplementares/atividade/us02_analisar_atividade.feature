Feature: Analisar Atividade Complementar

  Scenario: Deferir atividade dentro do limite da categoria
    Given uma atividade complementar pendente cadastrada para análise
    And o deferimento não excede o limite da categoria
    When o coordenador defere a atividade com 30 horas aprovadas
    Then a atividade deve ter status DEFERIDA com 30 horas aprovadas

  Scenario: Rejeitar deferimento que excede o limite da categoria
    Given uma atividade complementar pendente cadastrada para análise
    And o deferimento excede o limite da categoria
    When o coordenador tenta deferir a atividade com 30 horas
    Then deve ser lançada uma exceção de limite de categoria excedido

  Scenario: Indeferir atividade complementar
    Given uma atividade complementar pendente cadastrada para análise
    When o coordenador indefere a atividade com justificativa "Certificado inválido"
    Then a atividade deve ter status INDEFERIDA
