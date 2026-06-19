#language: pt
Funcionalidade: Acompanhar status das solicitações

  Cenário: Estudante consulta suas solicitações acadêmicas
    Dado um estudante com solicitações cadastradas
    Quando o estudante consulta suas solicitações
    Então o sistema retorna a lista de solicitações do estudante

  Cenário: Estudante sem solicitações consulta suas solicitações
    Dado um estudante sem solicitações cadastradas
    Quando o estudante consulta suas solicitações
    Então o sistema retorna uma lista vazia
