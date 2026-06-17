# language: pt
Funcionalidade: Comportamentos dinâmicos de turma via Decorator

  Cenário: Estudante entra na lista de espera de uma turma
    Dado uma turma ofertada com lista de espera habilitada
    Quando o estudante de id 1 entra na lista de espera
    Então a lista de espera contém 1 estudante

  Cenário: Estudante tenta entrar duas vezes na lista de espera
    Dado uma turma ofertada com lista de espera habilitada
    Quando o estudante de id 1 entra na lista de espera
    E o estudante de id 1 tenta entrar novamente
    Então o sistema rejeita a entrada duplicada na lista de espera

  Cenário: Turma online recebe link de acesso válido
    Dado uma turma EAD com decorator online
    Quando o link de acesso é definido como "https://meet.google.com/abc"
    Então o link de acesso da turma online é "https://meet.google.com/abc"
