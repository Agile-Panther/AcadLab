#language: pt
Funcionalidade: Registrar Pagamento de Cobrança

  Cenário: Registrar pagamento de cobrança aberta
    Dado uma cobrança aberta no sistema com valor 1500.00 para o contrato 20
    Quando registro o pagamento de 1500.00 com referência "TED-2025-001"
    Então a cobrança deve ter status PAGA
    E o pagamento deve estar com status CONFIRMADO
