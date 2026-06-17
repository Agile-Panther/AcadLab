# language: pt

Funcionalidade: Solicitar análise de conclusão de curso
  Como estudante
  Quero solicitar a análise de conclusão do meu curso
  Para iniciar formalmente o processo de verificação de aptidão para colação de grau

  Cenário: Estudante solicita análise com último período encerrado e sem pendências
    Dado um estudante com último período letivo encerrado e sem pendências
    Quando o estudante solicita análise de conclusão de curso
    Então a análise de integralização é iniciada com sucesso
    E o status da integralização é "EM_ANALISE"

  Cenário: Estudante com período letivo não encerrado não pode solicitar análise
    Dado um estudante com período letivo ainda em andamento
    Quando o estudante tenta solicitar análise de conclusão de curso
    Então o sistema rejeita a solicitação por período não encerrado

  Cenário: Estudante com pendências acadêmicas não pode solicitar análise
    Dado um estudante com pendências acadêmicas registradas
    Quando o estudante tenta solicitar análise de conclusão de curso
    Então o sistema rejeita a solicitação por pendências existentes
