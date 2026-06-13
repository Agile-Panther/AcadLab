Feature: Encerrar período letivo

  Scenario: Secretaria encerra período letivo sem pendências
    Given um período letivo cadastrado pronto para encerramento
    And sem pendências que impedem o encerramento
    When a secretaria encerra o período letivo
    Then o período letivo deve ter status encerrado

  Scenario: Sistema rejeita encerramento com pendências abertas
    Given um período letivo cadastrado pronto para encerramento
    And com pendências que impedem o encerramento
    When a secretaria tenta encerrar o período letivo
    Then o sistema rejeita o encerramento informando pendências

  Scenario: Sistema rejeita encerramento de período já encerrado
    Given um período letivo já encerrado anteriormente
    When a secretaria tenta encerrar o período letivo
    Then o sistema rejeita o encerramento informando status inválido
