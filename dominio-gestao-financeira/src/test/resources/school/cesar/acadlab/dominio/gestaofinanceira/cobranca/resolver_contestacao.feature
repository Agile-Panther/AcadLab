# language: pt

Funcionalidade: Resolver Contestação de Cobrança

  Cenário: Indeferir contestação mantém o valor
    Dado uma cobrança contestada pelo estudante 1 no contrato 50
    Quando o setor financeiro indefere a contestação com parecer "Cobrança correta"
    Então a contestação deve ter status "INDEFERIDA"
    E a cobrança deve retornar ao status ABERTA após resolução
    E o valor atual da cobrança permanece "1500.00"

  Cenário: Deferir com percentual reduz o valor
    Dado uma cobrança contestada pelo estudante 1 no contrato 50
    Quando o setor financeiro defere a contestação com 20 por cento e parecer "Ajuste deferido"
    Então a contestação deve ter status "DEFERIDA"
    E o valor atual da cobrança permanece "1200.00"

  Cenário: Deferir com valor absoluto define o valor
    Dado uma cobrança contestada pelo estudante 1 no contrato 50
    Quando o setor financeiro defere a contestação com o valor "1000.00" e parecer "Novo valor"
    Então a contestação deve ter status "DEFERIDA"
    E o valor atual da cobrança permanece "1000.00"

  Cenário: Rejeitar resolução de cobrança sem contestação
    Dado uma cobrança aberta sem contestação para o contrato 51
    Quando o setor financeiro tenta indeferir a contestação
    Então o sistema deve rejeitar informando "não há contestação registrada"

  Cenário: Rejeitar segunda resolução de contestação já resolvida
    Dado uma cobrança com contestação já resolvida para o contrato 52
    Quando o setor financeiro tenta indeferir a contestação novamente
    Então o sistema deve rejeitar informando "contestação já foi resolvida"
