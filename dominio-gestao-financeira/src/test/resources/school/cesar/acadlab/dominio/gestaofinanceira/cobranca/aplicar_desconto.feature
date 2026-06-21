# language: pt

Funcionalidade: Aplicar Desconto em Cobrança

  Cenário: Aplicar desconto com autorização válida
    Dado uma cobrança aberta de 1500.00 para o estudante 10
    E a autorização "AUTH-VALID-001" é válida
    Quando aplico um desconto de 10 por cento com autorização "AUTH-VALID-001"
    Então o valor atual da cobrança deve ser 1350.00

  Cenário: Rejeitar desconto com autorização inválida
    Dado uma cobrança aberta de 1500.00 para o estudante 11
    Quando tento aplicar um desconto de 10 por cento com autorização "AUTH-INVALIDA"
    Então o sistema deve rejeitar informando "autorização inválida para aplicação de desconto"
