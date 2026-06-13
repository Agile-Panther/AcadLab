Feature: Editar período letivo

  Scenario: Secretaria edita datas de período não iniciado
    Given um período letivo cadastrado editável
    When a secretaria edita o período letivo para as datas de "15/03/2025" a "30/07/2025"
    Then o período letivo deve ter data de início "15/03/2025" e data de fim "30/07/2025"

  Scenario: Sistema rejeita edição de período já encerrado
    Given um período letivo com status encerrado
    When a secretaria tenta editar o período letivo para as datas de "15/03/2025" a "30/07/2025"
    Then o sistema rejeita a edição informando status inválido
