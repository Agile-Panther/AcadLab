# language: pt

Funcionalidade: Cancelar Cobrança

  Cenário: Cancelar cobrança aberta com motivo
    Dado uma cobrança aberta para cancelamento no contrato 60
    Quando o operador cancela a cobrança com motivo "Matrícula cancelada"
    Então a cobrança deve ter status CANCELADA

  Cenário: Rejeitar cancelamento de cobrança já paga
    Dado uma cobrança paga para cancelamento no contrato 61
    Quando o operador tenta cancelar a cobrança paga
    Então o sistema deve rejeitar informando "não é possível cancelar uma cobrança já paga"
