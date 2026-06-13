Feature: Cancelar Submissão de Atividade Complementar

  Scenario: Cancelar atividade com status pendente de análise
    Given uma atividade complementar com status pendente aguardando cancelamento
    When o estudante solicita o cancelamento da submissão
    Then a atividade deve ter status CANCELADA

  Scenario: Rejeitar cancelamento de atividade já analisada
    Given uma atividade complementar com status deferida aguardando cancelamento
    When o estudante tenta solicitar o cancelamento da submissão
    Then deve ser lançada uma exceção de cancelamento inválido
