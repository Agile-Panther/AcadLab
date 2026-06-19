# language: pt

Funcionalidade: Gerenciar cadastro de professores

  Cenário: Secretaria inativa um professor ativo
    Dado um professor ativo cadastrado
    Quando a secretaria inativa o professor
    Então o professor passa a ter status inativo

  Cenário: Coordenação tenta vincular professor inativo a uma turma
    Dado um professor ativo cadastrado
    Quando a coordenação tenta vincular o professor inativo a uma turma
    Então o sistema deve rejeitar informando "professor está inativo e não pode ser vinculado à turma"
