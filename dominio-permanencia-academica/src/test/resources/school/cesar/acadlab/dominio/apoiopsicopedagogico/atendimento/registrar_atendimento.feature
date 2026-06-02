Feature: Registrar atendimento em caso psicopedagógico

  Scenario: Atendimento registrado com sucesso
    Given um caso psicopedagógico com triagem realizada
    When o psicopedagogo registra um atendimento no caso
    Then o sistema registra o atendimento e atualiza o status do caso

  Scenario: Registrar atendimento em caso inexistente falha
    Given um caso psicopedagógico que não existe no sistema
    When o psicopedagogo tenta registrar um atendimento no caso inexistente
    Then o sistema informa que o caso não foi encontrado
