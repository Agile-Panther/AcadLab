Feature: Acompanhar status das solicitações

  Scenario: Estudante consulta suas solicitações acadêmicas
    Given um estudante com solicitações cadastradas
    When o estudante consulta suas solicitações
    Then o sistema retorna a lista de solicitações do estudante

  Scenario: Estudante sem solicitações consulta suas solicitações
    Given um estudante sem solicitações cadastradas
    When o estudante consulta suas solicitações
    Then o sistema retorna uma lista vazia
