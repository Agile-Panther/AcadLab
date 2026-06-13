Feature: Editar período letivo

  Scenario: Secretaria edita datas de período não iniciado
    Given um período letivo cadastrado editável
    When a secretaria edita o período letivo para as datas de "15/03/2028" a "30/07/2028"
    Then o período letivo deve ter data de início "15/03/2028" e data de fim "30/07/2028"

  Scenario: Sistema rejeita edição de período já encerrado
    Given um período letivo com status encerrado
    When a secretaria tenta editar o período letivo para as datas de "15/03/2028" a "30/07/2028"
    Then o sistema rejeita a edição informando status inválido

  Scenario: Sistema rejeita edição de período em andamento
    Given um período letivo em andamento para tentativa de edição
    When a secretaria tenta editar o período letivo para as datas de "15/03/2028" a "30/07/2028"
    Then o sistema rejeita a edição informando status inválido
