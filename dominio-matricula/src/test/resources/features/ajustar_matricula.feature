#language: pt
Funcionalidade: Realizar ajuste de matrícula
  Como Estudante, quero cancelar disciplinas da minha matrícula durante o período de ajuste,
  para adequar minha grade após a confirmação inicial.

  Contexto:
    Dado que existe uma matrícula confirmada para o estudante 1 no período letivo 1

  Cenário: RN-8 Cancelar disciplina dentro da janela de ajuste
    Quando o estudante cancela a turma 10 dentro da janela de ajuste
    Então o item da turma 10 deve ter status "CANCELADO"

  Cenário: RN-8 Tentar cancelar disciplina fora da janela de ajuste
    Quando o estudante tenta cancelar a turma 10 fora da janela de ajuste
    Então deve ser lançado o erro "janela de ajuste"
