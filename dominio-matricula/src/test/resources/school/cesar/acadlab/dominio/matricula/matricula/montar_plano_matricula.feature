#language: pt
Funcionalidade: Montar plano de matrícula
  Como Estudante, quero montar meu plano de matrícula selecionando as turmas
  que desejo cursar, para planejar minha grade antes de confirmar a matrícula.

  Contexto:
    Dado que existe uma matrícula em montagem para o estudante 1 no período letivo 1

  Cenário: RN-1 Adicionar disciplina dentro da janela de matrícula
    Quando o estudante adiciona a turma 10 com 4 créditos dentro da janela de matrícula
    Então a matrícula deve conter 1 item

  Cenário: RN-1 Tentar adicionar disciplina fora da janela de matrícula
    Quando o estudante tenta adicionar a turma 10 fora da janela de matrícula
    Então deve ser lançado o erro "janela de matrícula"

  Cenário: RN-2 Tentar adicionar disciplina sem cumprir pré-requisitos
    Quando o estudante tenta adicionar a turma 10 sem cumprir os pré-requisitos
    Então deve ser lançado o erro "pré-requisitos"

  Cenário: RN-3 Tentar adicionar disciplina sem correquisitos no plano
    Quando o estudante tenta adicionar a turma 10 sem correquisitos no plano
    Então deve ser lançado o erro "correquisitos"

  Cenário: RN-4 Tentar adicionar disciplina que excede o limite de créditos
    Quando o estudante tenta adicionar uma disciplina que excede o limite de créditos
    Então deve ser lançado o erro "limite máximo de créditos"

  Cenário: RN-5 Tentar adicionar disciplina com pendências acadêmicas
    Quando o estudante tenta adicionar a turma 10 com pendências acadêmicas impeditivas
    Então deve ser lançado o erro "pendências acadêmicas"
