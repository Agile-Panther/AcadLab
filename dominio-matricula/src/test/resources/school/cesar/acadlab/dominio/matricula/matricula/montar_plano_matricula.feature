# language: pt

Funcionalidade: Montar plano de matrícula

  Contexto:
    Dado que existe uma matrícula em montagem para o estudante 1 no período letivo 1

  Cenário: Adicionar disciplina dentro da janela de matrícula
    Quando o estudante adiciona a turma 10 com 4 créditos dentro da janela de matrícula
    Então a matrícula deve conter 1 item

  Cenário: Tentar adicionar disciplina fora da janela de matrícula
    Quando o estudante tenta adicionar a turma 10 fora da janela de matrícula
    Então o sistema deve rejeitar informando "fora da janela de matrícula"

  Cenário: Tentar adicionar disciplina sem cumprir pré-requisitos
    Quando o estudante tenta adicionar a turma 10 sem cumprir os pré-requisitos
    Então o sistema deve rejeitar informando "pré-requisitos não cumpridos"

  Cenário: Tentar adicionar disciplina sem correquisitos no plano
    Quando o estudante tenta adicionar a turma 10 sem correquisitos no plano
    Então o sistema deve rejeitar informando "correquisitos ausentes no plano"

  Cenário: Tentar adicionar disciplina que excede o limite de créditos
    Quando o estudante tenta adicionar uma disciplina que excede o limite de créditos
    Então o sistema deve rejeitar informando "limite máximo de créditos atingido"

  Cenário: Tentar adicionar disciplina com pendências acadêmicas
    Quando o estudante tenta adicionar a turma 10 com pendências acadêmicas impeditivas
    Então o sistema deve rejeitar informando "estudante possui pendências acadêmicas impeditivas"
