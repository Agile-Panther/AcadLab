# language: pt

Funcionalidade: Consultar histórico oficial do estudante

  Cenário: Histórico oficial retorna registros consolidados de períodos encerrados
    Dado um histórico com um registro consolidado de turma encerrada
    Quando o sistema consulta o histórico oficial do estudante
    Então o histórico oficial contém 1 registro consolidado

  Cenário: Consulta de histórico oficial de estudante inexistente lança erro
    Dado que não existe histórico cadastrado para o estudante
    Quando o sistema tenta consultar o histórico oficial do estudante
    Então o sistema deve rejeitar informando "estudante não possui histórico cadastrado"
