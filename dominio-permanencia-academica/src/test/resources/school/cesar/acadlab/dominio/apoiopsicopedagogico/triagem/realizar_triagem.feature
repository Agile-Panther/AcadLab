# language: pt

Funcionalidade: Realizar triagem de caso psicopedagógico

  Cenário: Triagem realizada com sucesso
    Dado um caso psicopedagógico aberto sem triagem
    Quando o psicopedagogo realiza a triagem do caso
    Então o sistema registra a triagem no caso

  Cenário: Registrar atendimento em caso sem triagem falha
    Dado um caso psicopedagógico aberto sem triagem
    Quando o psicopedagogo tenta registrar um atendimento sem realizar triagem
    Então o sistema deve rejeitar informando "caso precisa passar por triagem antes do primeiro atendimento"
