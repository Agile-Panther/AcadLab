# language: pt

Funcionalidade: Realizar ajuste de matrícula

  Contexto:
    Dado que existe uma matrícula confirmada para o estudante 1 no período letivo 1

  Cenário: Cancelar disciplina dentro da janela de ajuste
    Quando o estudante cancela a turma 10 dentro da janela de ajuste
    Então o item da turma 10 deve ter status "CANCELADO"

  Cenário: Tentar cancelar disciplina fora da janela de ajuste
    Quando o estudante tenta cancelar a turma 10 fora da janela de ajuste
    Então o sistema deve rejeitar informando "fora da janela de ajuste de matrícula"
