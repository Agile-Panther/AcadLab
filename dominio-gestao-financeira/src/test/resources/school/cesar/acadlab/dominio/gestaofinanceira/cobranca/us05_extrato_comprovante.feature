#language: pt
Funcionalidade: Extrato e Comprovante de Pagamento

  Cenário: Consultar extrato com múltiplas cobranças do contrato
    Dado o contrato 30 possui 2 cobranças geradas
    Quando consulto o extrato do contrato 30
    Então o extrato deve conter 2 cobranças

  Cenário: Emitir comprovante de cobrança com pagamento confirmado
    Dado uma cobrança paga com referência "TED-COMPROV-001" para o contrato 31
    Quando solicito o comprovante da cobrança
    Então o comprovante deve conter a referência "TED-COMPROV-001"

  Cenário: Rejeitar emissão de comprovante sem pagamento
    Dado uma cobrança aberta sem pagamento para o contrato 32
    Quando solicito o comprovante da cobrança sem pagamento
    Então deve ser lançada uma exceção de comprovante indisponível
