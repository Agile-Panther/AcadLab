Feature: Registrar Pagamento de Cobrança

  Scenario: Registrar pagamento de cobrança aberta
    Given uma cobrança aberta no sistema com valor 1500.00 para o contrato 20
    When registro o pagamento de 1500.00 com referência "TED-2025-001"
    Then a cobrança deve ter status PAGA
    And o pagamento deve estar com status CONFIRMADO
