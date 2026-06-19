# language: pt

Funcionalidade: Registrar frequência dos estudantes

  Cenário: Professor registra presença de estudante com matrícula ativa
    Dado um diário com estudante matriculado ativo e aula registrada
    Quando o professor responsável registra a frequência do estudante ativo
    Então o lançamento de frequência é salvo para o estudante

  Cenário: Frequência de estudante não matriculado é rejeitada
    Dado um diário sem estudantes matriculados e com aula registrada
    Quando o professor tenta registrar frequência de estudante não matriculado
    Então o sistema deve rejeitar informando "estudante não está matriculado na turma"

  Cenário: Professor não responsável não pode registrar frequência
    Dado um diário com estudante matriculado ativo e aula registrada
    Quando outro professor tenta registrar a frequência do estudante
    Então o sistema deve rejeitar informando "professor não é o responsável pelo diário"
