Feature: Analisar solicitações acadêmicas

  Scenario: Secretaria defere solicitação sem impacto acadêmico e conclui
    Given uma solicitação pendente de análise
    When a secretaria inicia a análise da solicitação
    And a secretaria defere a solicitação sem impacto acadêmico
    And a secretaria conclui a solicitação
    Then a solicitação é concluída com sucesso

  Scenario: Secretaria defere solicitação com impacto acadêmico sem vincular alterações
    Given uma solicitação pendente de análise
    When a secretaria inicia a análise da solicitação
    And a secretaria defere a solicitação com impacto acadêmico
    And a secretaria tenta concluir a solicitação
    Then o sistema rejeita a conclusão por falta de vinculação de alterações

  Scenario: Secretaria defere solicitação com impacto e vincula alterações antes de concluir
    Given uma solicitação pendente de análise
    When a secretaria inicia a análise da solicitação
    And a secretaria defere a solicitação com impacto acadêmico
    And as alterações são vinculadas ao protocolo
    And a secretaria conclui a solicitação
    Then a solicitação é concluída com sucesso

  Scenario: Secretaria indefere solicitação com justificativa
    Given uma solicitação pendente de análise
    When a secretaria inicia a análise da solicitação
    And a secretaria indefere a solicitação
    Then a solicitação é indeferida com justificativa registrada
