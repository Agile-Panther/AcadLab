#language: pt
Funcionalidade: Solicitar exceção de matrícula
  Como Estudante, quero solicitar uma exceção às regras de matrícula,
  para que a coordenação avalie e decida sobre casos específicos bloqueados.

  Contexto:
    Dado que existe uma matrícula em montagem para o estudante 1 no período letivo 1

  Cenário: RN-10 Adicionar disciplina após exceção deferida pela coordenação
    Dado que o estudante solicitou exceção para a disciplina 5 com motivo "Disciplina especial"
    E a coordenação deferiu a exceção para a disciplina 5
    Quando o estudante adiciona a turma 10 sem cumprir pré-requisitos mas com exceção deferida
    Então a matrícula deve conter 1 item

  Cenário: RN-10 Tentar adicionar disciplina com exceção não deferida
    Dado que o estudante solicitou exceção para a disciplina 5 com motivo "Disciplina especial"
    Quando o estudante tenta adicionar a turma 10 sem pré-requisitos e sem exceção deferida
    Então deve ser lançado o erro "pré-requisitos"
