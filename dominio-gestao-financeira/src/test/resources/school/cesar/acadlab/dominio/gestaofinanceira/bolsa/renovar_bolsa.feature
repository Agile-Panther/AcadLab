# language: pt

Funcionalidade: Renovar bolsa

  Cenário: Solicitar renovação coloca a bolsa em renovação
    Dado uma bolsa ATIVA do estudante 1
    Quando solicito a renovação da bolsa
    Então a bolsa está com status "EM_RENOVACAO"
