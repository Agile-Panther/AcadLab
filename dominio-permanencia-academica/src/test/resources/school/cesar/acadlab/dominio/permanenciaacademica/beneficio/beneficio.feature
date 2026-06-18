Feature: Gerenciar benefício de permanência acadêmica

  Scenario: Estudante solicita renovação dentro do prazo
    Given um estudante possui um benefício ativo com prazo de renovação futuro
    When o estudante solicita a renovação do benefício
    Then o sistema registra a solicitação de renovação

  Scenario: Renovação falha quando fora do prazo
    Given um estudante possui um benefício ativo com prazo de renovação já vencido
    When o estudante tenta solicitar a renovação do benefício
    Then o sistema informa que o prazo de renovação já encerrou

  Scenario: Benefício suspenso por não cumprimento dos critérios mínimos
    Given um estudante possui um benefício ativo
    When o sistema suspende o benefício por não cumprimento dos critérios
    Then o status do benefício é atualizado para suspenso
    And um evento de suspensão é publicado no barramento

  Scenario: Benefício cancelado por não cumprimento dos critérios mínimos
    Given um estudante possui um benefício ativo
    When o sistema cancela o benefício por não cumprimento dos critérios
    Then o status do benefício é atualizado para cancelado
    And um evento de cancelamento é publicado no barramento
