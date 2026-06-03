Feature: Realizar triagem de caso psicopedagógico

  Scenario: Triagem realizada com sucesso
    Given um caso psicopedagógico aberto sem triagem
    When o psicopedagogo realiza a triagem do caso
    Then o sistema registra a triagem no caso

  Scenario: Registrar atendimento em caso sem triagem falha
    Given um caso psicopedagógico aberto sem triagem
    When o psicopedagogo tenta registrar um atendimento sem realizar triagem
    Then o sistema informa que o caso precisa passar por triagem antes do atendimento
