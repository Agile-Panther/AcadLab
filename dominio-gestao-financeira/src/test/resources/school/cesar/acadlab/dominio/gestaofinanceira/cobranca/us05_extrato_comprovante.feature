Feature: Extrato e Comprovante de Pagamento

  Scenario: Consultar extrato com múltiplas cobranças do contrato
    Given o contrato 30 possui 2 cobranças geradas
    When consulto o extrato do contrato 30
    Then o extrato deve conter 2 cobranças

  Scenario: Emitir comprovante de cobrança com pagamento confirmado
    Given uma cobrança paga com referência "TED-COMPROV-001" para o contrato 31
    When solicito o comprovante da cobrança
    Then o comprovante deve conter a referência "TED-COMPROV-001"

  Scenario: Rejeitar emissão de comprovante sem pagamento
    Given uma cobrança aberta sem pagamento para o contrato 32
    When solicito o comprovante da cobrança sem pagamento
    Then deve ser lançada uma exceção de comprovante indisponível
