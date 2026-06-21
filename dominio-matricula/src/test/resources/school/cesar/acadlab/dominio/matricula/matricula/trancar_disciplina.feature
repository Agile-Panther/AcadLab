# language: pt

Funcionalidade: Trancar disciplina

  Contexto:
    Dado que existe uma matrícula confirmada para o estudante 1 no período letivo 1

  Cenário: Trancar disciplina dentro da janela de trancamento
    Quando o estudante tranca a turma 10 dentro da janela de trancamento
    Então o item da turma 10 deve ter status "TRANCADO"

  Cenário: Tentar trancar disciplina fora da janela de trancamento
    Quando o estudante tenta trancar a turma 10 fora da janela de trancamento
    Então o sistema deve rejeitar informando "fora da janela de trancamento"
