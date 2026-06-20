# language: pt

Funcionalidade: Resolver Contestação de Cobrança

  Cenário: Resolver contestação pendente com parecer
    Dado uma cobrança contestada pelo estudante 1 no contrato 50
    Quando o setor financeiro resolve a contestação com parecer "Valor conferido e correto"
    Então a contestação deve ter status RESOLVIDA
    E a cobrança deve retornar ao status ABERTA após resolução

  Cenário: Rejeitar resolução de cobrança sem contestação
    Dado uma cobrança aberta sem contestação para o contrato 51
    Quando o setor financeiro tenta resolver a contestação
    Então o sistema deve rejeitar informando "não há contestação registrada"

  Cenário: Rejeitar segunda resolução de contestação já resolvida
    Dado uma cobrança com contestação já resolvida para o contrato 52
    Quando o setor financeiro tenta resolver a contestação novamente
    Então o sistema deve rejeitar informando "contestação já foi resolvida"
