Feature: Complementar solicitação com pendências

  Scenario: Estudante complementa solicitação pendente de complementação
    Given uma solicitação com status "PENDENTE_COMPLEMENTACAO"
    When o estudante complementa a solicitação com um documento
    Then a solicitação volta para status "PENDENTE_ANALISE"

  Scenario: Estudante tenta complementar solicitação concluída
    Given uma solicitação com status "CONCLUIDA"
    When o estudante tenta complementar a solicitação
    Then o sistema rejeita a complementação

  Scenario: Estudante tenta complementar solicitação indeferida
    Given uma solicitação com status "INDEFERIDA"
    When o estudante tenta complementar a solicitação
    Then o sistema rejeita a complementação
