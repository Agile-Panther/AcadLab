# language: pt

Funcionalidade: Gerenciar status da matriz curricular

  Cenário: Ativar matriz quando não existe matriz ativa para o curso
    Dado uma matriz curricular com carga horária suficiente para o curso 20
    Quando o coordenador ativa a matriz
    Então o status da matriz deve ser ATIVA

  Cenário: Ativar matriz quando já existe uma ativa é rejeitado
    Dado uma matriz curricular já ativa para o curso 20
    E uma segunda matriz curricular com carga horária suficiente para o curso 20
    Quando o coordenador tenta ativar a segunda matriz
    Então o sistema deve rejeitar informando "já existe uma matriz curricular ativa para este curso"
