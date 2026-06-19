# language: pt

Funcionalidade: Registrar atendimento em caso psicopedagógico

  Cenário: Atendimento registrado com sucesso
    Dado um caso psicopedagógico com triagem realizada
    Quando o psicopedagogo registra um atendimento no caso
    Então o sistema registra o atendimento e atualiza o status do caso

  Cenário: Registrar atendimento em caso inexistente falha
    Dado um caso psicopedagógico que não existe no sistema
    Quando o psicopedagogo tenta registrar um atendimento no caso inexistente
    Então o sistema deve rejeitar informando "caso psicopedagógico não encontrado"
