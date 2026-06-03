Feature: Registrar cerimônia de colação de grau

  Scenario: Secretaria registra colação para estudante com aptidão aprovada
    Given um estudante com aptidão formalmente aprovada
    When a secretaria registra a colação de grau com data válida
    Then a colação de grau é registrada com sucesso

  Scenario: Secretaria tenta registrar colação sem aptidão aprovada
    Given um estudante sem aptidão aprovada
    When a secretaria tenta registrar colação sem aptidão aprovada
    Then o sistema rejeita o registro da colação por ausência de aptidão
