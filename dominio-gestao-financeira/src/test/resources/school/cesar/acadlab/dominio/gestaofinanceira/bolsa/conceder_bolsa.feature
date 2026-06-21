# language: pt

Funcionalidade: Conceder bolsa

  Cenário: Conceder bolsa de mérito ativa
    Quando concedo uma bolsa MERITO de 50 por cento ao estudante 1 com validade "2025-12-31"
    Então a bolsa está com status "ATIVA"
