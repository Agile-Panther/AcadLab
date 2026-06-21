# language: pt

Funcionalidade: Trancar período letivo

  Contexto:
    Dado que existe uma matrícula confirmada para o estudante 1 no período letivo 1

  Cenário: Trancar período dentro da janela com trancamentos disponíveis
    Quando o estudante tranca o período dentro da janela com 0 de 3 trancamentos utilizados
    Então a matrícula deve estar com status "TRANCADA_PERIODO"

  Cenário: Tentar trancar período fora da janela de trancamento
    Quando o estudante tenta trancar o período fora da janela de trancamento
    Então o sistema deve rejeitar informando "fora da janela de trancamento"

  Cenário: Tentar trancar período com limite de trancamentos atingido
    Quando o estudante tenta trancar o período com o limite de 3 trancamentos atingido
    Então o sistema deve rejeitar informando "limite de trancamentos de período atingido"
