Feature: Cancelar Pagamento de Cobrança

  Scenario: Cancelar pagamento confirmado e reativar cobrança
    Given uma cobrança confirmada com referência "PAG-CANCEL-001" para o contrato 40
    When o operador cancela o pagamento com justificativa "Solicitação de estorno do estudante"
    Then o pagamento deve ter status CANCELADO
    And a cobrança deve voltar para o status ABERTA

  Scenario: Rejeitar cancelamento de pagamento já cancelado
    Given uma cobrança com pagamento já cancelado no contrato 41
    When o operador tenta cancelar o pagamento já cancelado
    Then deve ser lançada uma exceção de pagamento não cancelável
