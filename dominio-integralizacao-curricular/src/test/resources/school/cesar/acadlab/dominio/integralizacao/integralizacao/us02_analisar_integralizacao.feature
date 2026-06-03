Feature: Analisar integralização curricular e registrar resultado

  Scenario: Secretaria analisa integralização com todos os requisitos cumpridos e registra resultado apto
    Given uma solicitação de análise de integralização iniciada
    When a secretaria gera o checklist com todos os requisitos cumpridos
    Then a integralização é registrada com resultado apto

  Scenario: Secretaria analisa integralização com pendências e registra resultado inapto
    Given uma solicitação de análise de integralização iniciada
    When a secretaria gera o checklist com pendências e registra resultado inapto
    Then a integralização é registrada com resultado inapto

  Scenario: Coordenador aprova aptidão do estudante com resultado apto
    Given uma solicitação de análise de integralização iniciada
    When a secretaria gera o checklist com todos os requisitos cumpridos
    And o coordenador aprova a aptidão do estudante
    Then a aptidão do estudante é formalmente aprovada
