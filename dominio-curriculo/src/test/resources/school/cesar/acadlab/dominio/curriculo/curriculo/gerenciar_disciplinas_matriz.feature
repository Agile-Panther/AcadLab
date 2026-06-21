# language: pt

Funcionalidade: Gerenciar disciplinas da matriz curricular

  Cenário: Adicionar disciplina a uma matriz em rascunho
    Dado uma matriz curricular com status RASCUNHO
    Quando o coordenador adiciona a disciplina com id 5 à matriz
    Então a matriz deve conter a disciplina com id 5

  Cenário: Adicionar disciplina duplicada é rejeitado
    Dado uma matriz curricular com status RASCUNHO com a disciplina 5 já adicionada
    Quando o coordenador tenta adicionar a disciplina 5 novamente
    Então o sistema deve rejeitar informando "disciplina já está na matriz curricular"

  Cenário: Remover disciplina de matriz em rascunho sem turmas vinculadas
    Dado uma matriz curricular com status RASCUNHO com a disciplina 5 já adicionada
    Quando o coordenador remove a disciplina 5 da matriz
    Então a matriz não deve conter a disciplina com id 5

  Cenário: Remover disciplina com turmas vinculadas é rejeitado
    Dado uma matriz curricular com status RASCUNHO com a disciplina 5 já adicionada
    E a disciplina 5 possui turmas vinculadas
    Quando o coordenador tenta remover a disciplina 5
    Então o sistema deve rejeitar informando "disciplina não pode ser removida pois possui turmas vinculadas"

  Cenário: Adicionar disciplina em matriz ATIVA é rejeitado
    Dado uma matriz curricular ATIVA com as disciplinas 1 e 2
    Quando o coordenador adiciona a disciplina com id 9 à matriz
    Então o sistema deve rejeitar informando "não é possível alterar matriz ativa"

  Cenário: Remover disciplina de matriz ATIVA é rejeitado
    Dado uma matriz curricular ATIVA com as disciplinas 1 e 2
    Quando o coordenador tenta remover a disciplina 1
    Então o sistema deve rejeitar informando "não é possível alterar matriz ativa"
