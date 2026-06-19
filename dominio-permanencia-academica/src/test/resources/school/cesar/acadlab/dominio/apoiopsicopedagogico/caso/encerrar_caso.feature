#language: pt
Funcionalidade: Encerrar caso psicopedagógico

  Cenário: Caso encerrado com conclusão registrada
    Dado um caso psicopedagógico com atendimento de conclusão final registrado
    Quando o psicopedagogo encerra o caso
    Então o sistema encerra o caso e atualiza o status para encerrado

  Cenário: Encerrar caso sem conclusão ou encaminhamento falha
    Dado um caso psicopedagógico com atendimentos sem conclusão ou encaminhamento
    Quando o psicopedagogo tenta encerrar o caso
    Então o sistema informa que é necessário registrar uma conclusão ou encaminhamento final antes de encerrar

  Cenário: Encerrar caso com atendimento de encaminhamento registrado
    Dado um caso psicopedagógico com atendimento de encaminhamento final registrado
    Quando o psicopedagogo encerra o caso
    Então o sistema encerra o caso e atualiza o status para encerrado
