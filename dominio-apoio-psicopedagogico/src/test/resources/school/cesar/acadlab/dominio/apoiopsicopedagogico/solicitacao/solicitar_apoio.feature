# language: pt

Funcionalidade: Solicitar apoio psicopedagógico

  Cenário: Estudante sem caso ativo solicita apoio com sucesso
    Dado um estudante sem caso psicopedagógico ativo
    Quando o estudante solicita apoio psicopedagógico
    Então o sistema registra a solicitação com sucesso
    E um caso psicopedagógico é aberto para o estudante

  Cenário: Estudante com caso ativo tenta solicitar novamente
    Dado um estudante com caso psicopedagógico ativo
    Quando o estudante solicita apoio psicopedagógico
    Então o sistema deve rejeitar informando "estudante já possui um caso psicopedagógico ativo"

  Cenário: Estudante com caso encerrado solicita apoio e um novo caso é aberto
    Dado um estudante com caso psicopedagógico encerrado
    Quando o estudante solicita apoio psicopedagógico
    Então o sistema registra a solicitação com sucesso
    E o caso encerrado anterior permanece no histórico
    E um novo caso psicopedagógico é aberto para o estudante

  Cenário: Estudante reabre um caso encerrado
    Dado um estudante com caso psicopedagógico encerrado
    Quando o estudante reabre o caso encerrado
    Então o sistema reabre o caso psicopedagógico do estudante

  Cenário: Reabrir caso encerrado com caso ativo existente falha
    Dado um estudante com caso psicopedagógico encerrado
    E o estudante também possui um caso ativo
    Quando o estudante tenta reabrir o caso encerrado
    Então o sistema deve rejeitar informando "estudante já possui um caso psicopedagógico ativo"
