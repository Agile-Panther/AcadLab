# language: pt

Funcionalidade: Suspender e reativar bolsa

  Cenário: Suspender uma bolsa ativa
    Dado uma bolsa ATIVA do estudante 1
    Quando suspendo a bolsa
    Então a bolsa está com status "SUSPENSA"

  Cenário: Reativar uma bolsa suspensa
    Dado uma bolsa ATIVA do estudante 1
    E a bolsa está suspensa
    Quando reativo a bolsa
    Então a bolsa está com status "ATIVA"
