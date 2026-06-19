# language: pt

Funcionalidade: Cancelar candidatura a uma oportunidade de estágio

  Cenário: Estudante cancela candidatura em análise com sucesso
    Dado uma candidatura em análise
    Quando o estudante cancela a candidatura
    Então a candidatura é cancelada com sucesso

  Cenário: Estudante tenta cancelar candidatura já deferida
    Dado uma candidatura já deferida
    Quando o estudante tenta cancelar a candidatura
    Então o sistema deve rejeitar informando "candidatura não pode ser cancelada pois não está em análise"
