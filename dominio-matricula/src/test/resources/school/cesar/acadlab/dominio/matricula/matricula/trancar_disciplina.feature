#language: pt
Funcionalidade: Trancar disciplina
  Como Estudante, quero solicitar o trancamento de uma disciplina específica,
  para interromper minha participação nela sem ser reprovado por falta.

  Contexto:
    Dado que existe uma matrícula confirmada para o estudante 1 no período letivo 1

  Cenário: RN-9 Trancar disciplina dentro da janela de trancamento
    Quando o estudante tranca a turma 10 dentro da janela de trancamento
    Então o item da turma 10 deve ter status "TRANCADO"

  Cenário: RN-9 Tentar trancar disciplina fora da janela de trancamento
    Quando o estudante tenta trancar a turma 10 fora da janela de trancamento
    Então deve ser lançado o erro "janela de trancamento"
