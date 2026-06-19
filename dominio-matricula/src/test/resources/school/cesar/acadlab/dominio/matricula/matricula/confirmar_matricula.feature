#language: pt
Funcionalidade: Confirmar matrícula
  Como Estudante, quero confirmar minha matrícula nas turmas selecionadas no plano,
  para garantir minha vaga e formalizar meu vínculo com as disciplinas do período letivo.

  Contexto:
    Dado que existe uma matrícula em montagem para o estudante 1 no período letivo 1
    E o estudante adicionou a turma 10 com 4 créditos no horário segunda das "08:00" às "10:00"

  Cenário: RN-6 Confirmar matrícula com vagas disponíveis
    Quando o estudante confirma a matrícula com 30 vagas disponíveis na turma 10
    Então a matrícula deve estar com status "CONFIRMADA"

  Cenário: RN-6 Tentar confirmar matrícula sem vagas disponíveis
    Quando o estudante tenta confirmar a matrícula com 0 vagas disponíveis na turma 10
    Então deve ser lançado o erro "vagas disponíveis"

  Cenário: RN-7 Tentar confirmar matrícula com conflito de horário
    E o estudante adicionou a turma 20 com 4 créditos no horário segunda das "09:00" às "11:00"
    Quando o estudante tenta confirmar a matrícula com vagas nas duas turmas
    Então deve ser lançado o erro "conflito de horário"
