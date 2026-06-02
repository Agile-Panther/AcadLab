Feature: Encerrar caso psicopedagógico

  Scenario: Caso encerrado com conclusão registrada
    Given um caso psicopedagógico com atendimento de conclusão final registrado
    When o psicopedagogo encerra o caso
    Then o sistema encerra o caso e atualiza o status para encerrado

  Scenario: Encerrar caso sem conclusão ou encaminhamento falha
    Given um caso psicopedagógico com atendimentos sem conclusão ou encaminhamento
    When o psicopedagogo tenta encerrar o caso
    Then o sistema informa que é necessário registrar uma conclusão ou encaminhamento final antes de encerrar

  Scenario: Encerrar caso com atendimento de encaminhamento registrado
    Given um caso psicopedagógico com atendimento de encaminhamento final registrado
    When o psicopedagogo encerra o caso
    Then o sistema encerra o caso e atualiza o status para encerrado
