# language: pt

Funcionalidade: Aplicar desconto respaldado por bolsa (RN5)

  Cenário: Desconto aceito com bolsa ativa
    Dado uma bolsa MERITO ativa de 10 por cento para o estudante 7
    E uma cobrança aberta de 1000.00 para o estudante 7 contra o contrato 70
    Quando aplico o desconto da bolsa ativa na cobrança
    Então o valor atual da cobrança deve ser 900.00 reais

  Cenário: Desconto recusado sem bolsa
    E uma cobrança aberta de 1000.00 para o estudante 8 contra o contrato 80
    Quando tento aplicar um desconto de 10 por cento com a autorização "999"
    Então o desconto é recusado por autorização inválida
