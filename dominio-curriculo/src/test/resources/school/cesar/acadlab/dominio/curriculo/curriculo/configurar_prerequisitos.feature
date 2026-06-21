# language: pt

Funcionalidade: Configurar pré-requisitos e correquisitos

  Cenário: Adicionar pré-requisito entre disciplinas com sucesso
    Dado uma matriz curricular com duas disciplinas de ids 1 e 2
    Quando o coordenador define a disciplina 1 como pré-requisito da disciplina 2
    Então a disciplina 2 deve ter a disciplina 1 como pré-requisito

  Cenário: Adicionar pré-requisito cíclico é rejeitado
    Dado uma matriz curricular com duas disciplinas de ids 1 e 2
    E a disciplina 1 já é pré-requisito da disciplina 2
    Quando o coordenador tenta definir a disciplina 2 como pré-requisito da disciplina 1
    Então o sistema deve rejeitar informando "relação de pré-requisito cíclica detectada"

  Cenário: Adicionar correquisito da mesma matriz com sucesso
    Dado uma matriz curricular com duas disciplinas de ids 1 e 2
    Quando o coordenador define a disciplina 2 como correquisito da disciplina 1
    Então a disciplina 1 deve ter a disciplina 2 como correquisito

  Cenário: Adicionar correquisito de fora da matriz é rejeitado
    Dado uma matriz curricular com uma disciplina de id 1
    Quando o coordenador tenta definir a disciplina 99 como correquisito da disciplina 1
    Então o sistema deve rejeitar informando "disciplina correquisito não pertence à matriz curricular"

  Cenário: Adicionar pré-requisito em matriz ATIVA é rejeitado
    Dado uma matriz curricular ATIVA com as disciplinas 1 e 2
    Quando o coordenador tenta definir a disciplina 1 como pré-requisito da disciplina 2
    Então o sistema deve rejeitar informando "não é possível alterar matriz ativa"

  Cenário: Adicionar correquisito em matriz ATIVA é rejeitado
    Dado uma matriz curricular ATIVA com as disciplinas 1 e 2
    Quando o coordenador tenta definir a disciplina 1 como correquisito da disciplina 2
    Então o sistema deve rejeitar informando "não é possível alterar matriz ativa"
