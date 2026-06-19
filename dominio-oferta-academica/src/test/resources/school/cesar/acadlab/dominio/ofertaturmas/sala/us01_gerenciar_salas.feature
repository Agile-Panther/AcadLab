#language: pt
Funcionalidade: Gerenciar cadastro de salas

  Cenário: Secretaria inativa uma sala ativa
    Dado uma sala ativa cadastrada com capacidade para trinta pessoas
    Quando a secretaria inativa a sala
    Então a sala passa a ter status inativo

  Cenário: Secretaria tenta reduzir capacidade da sala abaixo de turma vinculada
    Dado uma sala ativa cadastrada com capacidade para trinta pessoas
    Quando a secretaria tenta reduzir a capacidade da sala para vinte pessoas havendo turma com trinta vagas
    Então o sistema rejeita a redução de capacidade da sala
