#language: pt
Funcionalidade: Contestar Cobrança

  Cenário: Estudante contesta sua própria cobrança
    Dado uma cobrança aberta para o estudante 1 no contrato 1
    Quando o estudante 1 contesta a cobrança com justificativa "Valor diverge do contrato"
    Então a cobrança deve ter status CONTESTADA

  Cenário: Rejeitar contestação de cobrança de outro estudante
    Dado uma cobrança aberta para o estudante 1 no contrato 1
    Quando o estudante 2 tenta contestar a cobrança do estudante 1
    Então deve ser lançada uma exceção de contestação indevida

  Cenário: Rejeitar segunda contestação pendente
    Dado uma cobrança aberta para o estudante 1 no contrato 1
    E o estudante 1 já contestou a cobrança anteriormente
    Quando o estudante 1 tenta contestar a cobrança novamente
    Então deve ser lançada uma exceção de contestação duplicada
