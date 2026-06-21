# language: pt

Funcionalidade: Criar matriz curricular

  Cenário: Coordenador cria matriz curricular com carga horária suficiente
    Dado um curso com id 10
    E uma nova matriz curricular chamada "Engenharia de Software" com carga horária mínima 2400 e créditos mínimos 160
    E uma disciplina com id 1 adicionada com carga horária 2400 e créditos 160
    Quando o coordenador ativa a matriz
    Então o status da matriz deve ser ATIVA

  Cenário: Ativar matriz com carga horária insuficiente é rejeitado
    Dado um curso com id 10
    E uma nova matriz curricular chamada "Engenharia de Software" com carga horária mínima 2400 e créditos mínimos 160
    E uma disciplina com id 1 adicionada com carga horária 60 e créditos 4
    Quando o coordenador tenta ativar a matriz
    Então o sistema deve rejeitar informando "carga horária total não atinge o mínimo exigido"
