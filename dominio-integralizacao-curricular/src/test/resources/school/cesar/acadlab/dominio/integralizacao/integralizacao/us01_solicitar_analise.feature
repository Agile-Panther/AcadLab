# language: pt

Funcionalidade: Solicitar análise de conclusão de curso

  Cenário: Estudante solicita análise com último período encerrado e sem pendências
    Dado um estudante com último período letivo encerrado e sem pendências
    Quando o estudante solicita análise de conclusão de curso
    Então a análise de integralização é iniciada com sucesso
    E o status da integralização é "EM_ANALISE"

  Cenário: Estudante com período letivo não encerrado não pode solicitar análise
    Dado um estudante com período letivo ainda em andamento
    Quando o estudante tenta solicitar análise de conclusão de curso
    Então o sistema deve rejeitar informando "período letivo ainda não foi encerrado"

  Cenário: Estudante com pendências acadêmicas ou documentais não pode solicitar análise
    Dado um estudante com pendências acadêmicas registradas
    Quando o estudante tenta solicitar análise de conclusão de curso
    Então o sistema deve rejeitar informando "estudante possui pendências acadêmicas ou documentais"
