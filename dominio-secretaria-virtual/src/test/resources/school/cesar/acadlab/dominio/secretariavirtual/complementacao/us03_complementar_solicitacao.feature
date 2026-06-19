# language: pt

Funcionalidade: Complementar solicitação com pendências

  Cenário: Estudante complementa solicitação pendente de complementação
    Dado uma solicitação com status "PENDENTE_COMPLEMENTACAO"
    Quando o estudante complementa a solicitação com um documento
    Então a solicitação volta para status "PENDENTE_ANALISE"

  Cenário: Estudante tenta complementar solicitação concluída
    Dado uma solicitação com status "CONCLUIDA"
    Quando o estudante tenta complementar a solicitação
    Então o sistema deve rejeitar informando "solicitação concluída não pode ser complementada"

  Cenário: Estudante tenta complementar solicitação indeferida
    Dado uma solicitação com status "INDEFERIDA"
    Quando o estudante tenta complementar a solicitação
    Então o sistema deve rejeitar informando "solicitação indeferida não pode ser complementada"
