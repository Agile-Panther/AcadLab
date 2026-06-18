Feature: Analisar inscrições e interpor recurso

  Scenario: Assistência estudantil defere inscrição com sucesso
    Given existe uma inscrição pendente no edital
    When a assistência estudantil defere a inscrição com pontuação 80
    Then o sistema atualiza o status da inscrição para deferida

  Scenario: Assistência estudantil indefere inscrição com sucesso
    Given existe uma inscrição pendente no edital
    When a assistência estudantil indefere a inscrição
    Then o sistema atualiza o status da inscrição para indeferida

  Scenario: Estudante interpõe recurso contra indeferimento dentro do prazo
    Given existe uma inscrição indeferida e o prazo de recurso está aberto
    When o estudante interpõe recurso contra o indeferimento
    Then o sistema registra o recurso e atualiza o status para recurso interposto

  Scenario: Recurso falha quando já foi interposto anteriormente
    Given existe uma inscrição com recurso já interposto
    When o estudante tenta interpor novo recurso
    Then o sistema informa que já foi interposto um recurso para esta inscrição
