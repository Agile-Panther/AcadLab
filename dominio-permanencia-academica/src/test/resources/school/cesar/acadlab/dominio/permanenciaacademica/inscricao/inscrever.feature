Feature: Inscrever-se em programa de permanência

  Scenario: Estudante se inscreve dentro do prazo e atendendo aos critérios
    Given existe um edital com inscrições abertas e o prazo atual é válido
    And o estudante atende aos critérios de elegibilidade
    When o estudante solicita inscrição no edital
    Then o sistema registra a inscrição com status pendente

  Scenario: Inscrição falha quando fora do prazo do edital
    Given existe um edital cujo prazo de inscrição já encerrou
    When o estudante tenta se inscrever no edital
    Then o sistema informa que a inscrição está fora do prazo

  Scenario: Inscrição falha quando estudante não atende os critérios de elegibilidade
    Given existe um edital com inscrições abertas e o prazo atual é válido
    And o estudante não atende aos critérios de elegibilidade
    When o estudante solicita inscrição no edital
    Then o sistema informa que o estudante não atende aos critérios de elegibilidade
