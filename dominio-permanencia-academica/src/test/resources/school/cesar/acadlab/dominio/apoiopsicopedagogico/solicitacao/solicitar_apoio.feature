Feature: Solicitar apoio psicopedagógico

  Scenario: Estudante sem caso ativo solicita apoio com sucesso
    Given um estudante sem caso psicopedagógico ativo
    When o estudante solicita apoio psicopedagógico
    Then o sistema registra a solicitação com sucesso
    And um caso psicopedagógico é aberto para o estudante

  Scenario: Estudante com caso ativo tenta solicitar novamente
    Given um estudante com caso psicopedagógico ativo
    When o estudante solicita apoio psicopedagógico
    Then o sistema informa que o estudante já possui um caso ativo

  Scenario: Estudante com caso encerrado solicita apoio e caso é reaberto
    Given um estudante com caso psicopedagógico encerrado
    When o estudante solicita apoio psicopedagógico
    Then o sistema reabre o caso psicopedagógico do estudante
