#language: pt
Funcionalidade: Cancelar Pagamento de Cobrança

  Cenário: Cancelar pagamento confirmado e reativar cobrança
    Dado uma cobrança confirmada com referência "PAG-CANCEL-001" para o contrato 40
    Quando o operador cancela o pagamento com justificativa "Solicitação de estorno do estudante"
    Então o pagamento deve ter status CANCELADO
    E a cobrança deve voltar para o status ABERTA

  Cenário: Rejeitar cancelamento de pagamento já cancelado
    Dado uma cobrança com pagamento já cancelado no contrato 41
    Quando o operador tenta cancelar o pagamento já cancelado
    Então deve ser lançada uma exceção de pagamento não cancelável
