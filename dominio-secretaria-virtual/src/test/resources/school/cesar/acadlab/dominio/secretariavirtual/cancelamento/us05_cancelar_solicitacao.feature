Feature: Cancelar solicitação acadêmica aberta

  Scenario: Estudante cancela solicitação pendente de análise
    Given uma solicitação com status pendente de análise para cancelamento
    When o estudante cancela a solicitação
    Then a solicitação é cancelada com sucesso

  Scenario: Estudante tenta cancelar solicitação em análise
    Given uma solicitação com status em análise para cancelamento
    When o estudante tenta cancelar a solicitação
    Then o sistema rejeita o cancelamento

  Scenario: Estudante tenta cancelar solicitação deferida
    Given uma solicitação com status deferida para cancelamento
    When o estudante tenta cancelar a solicitação
    Then o sistema rejeita o cancelamento
