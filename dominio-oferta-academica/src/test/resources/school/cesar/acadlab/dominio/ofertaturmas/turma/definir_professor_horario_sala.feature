# language: pt

Funcionalidade: Definir professor, horário e sala da turma com verificação de conflito

  Cenário: Coordenação vincula professor sem conflito de horário
    Dado uma turma planejada com horário segunda 08h às 10h no período 1
    E um professor sem turmas no mesmo período
    Quando a coordenação adiciona o horário e vincula o professor à turma
    Então o professor é vinculado com sucesso

  Cenário: Coordenação tenta vincular professor com conflito de horário
    Dado uma turma planejada com horário segunda 08h às 10h no período 1
    E o professor já tem turma com horário segunda 09h às 11h no mesmo período
    Quando a coordenação tenta vincular o professor com conflito
    Então o sistema deve rejeitar informando conflito de horário do professor

  Cenário: Coordenação tenta vincular sala com conflito de horário
    Dado uma turma planejada com horário segunda 08h às 10h no período 1
    E a sala já está alocada em turma com horário segunda 09h às 11h no mesmo período
    Quando a coordenação tenta vincular a sala com conflito
    Então o sistema deve rejeitar informando conflito de horário da sala
