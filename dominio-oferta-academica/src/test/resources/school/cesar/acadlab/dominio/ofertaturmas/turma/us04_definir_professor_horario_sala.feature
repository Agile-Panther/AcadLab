#language: pt
Funcionalidade: Definir professor, horario e sala da turma com verificacao de conflito

  Cenário: Coordenacao vincula professor sem conflito de horario
    Dado uma turma planejada com horario segunda 08h as 10h no periodo 1
    E um professor sem turmas no mesmo periodo
    Quando a coordenacao adiciona o horario e vincula o professor a turma
    Então o professor e vinculado com sucesso

  Cenário: Coordenacao tenta vincular professor com conflito de horario
    Dado uma turma planejada com horario segunda 08h as 10h no periodo 1
    E o professor ja tem turma com horario segunda 09h as 11h no mesmo periodo
    Quando a coordenacao tenta vincular o professor com conflito
    Então o sistema rejeita a vinculacao informando conflito de horario do professor

  Cenário: Coordenacao tenta vincular sala com conflito de horario
    Dado uma turma planejada com horario segunda 08h as 10h no periodo 1
    E a sala ja esta alocada em turma com horario segunda 09h as 11h no mesmo periodo
    Quando a coordenacao tenta vincular a sala com conflito
    Então o sistema rejeita a vinculacao informando conflito de horario da sala
