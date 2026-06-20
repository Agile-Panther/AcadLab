# language: pt

Funcionalidade: Gerar Cobrança e Nova Versão

  Cenário: Gerar cobrança para estudante com matrícula confirmada
    Dado o estudante 3 possui matrícula confirmada no período letivo 1
    Quando gero uma cobrança para o estudante 3 no contrato 2 com valor 1500.00
    Então a cobrança deve ser gerada com status ABERTA

  Cenário: Rejeitar geração de cobrança sem matrícula confirmada
    Dado o estudante 4 não possui matrícula confirmada no período letivo 1
    Quando tento gerar uma cobrança para o estudante 4 no contrato 3 com valor 1500.00
    Então o sistema deve rejeitar informando "estudante não possui matrícula confirmada no período"

  Cenário: Gerar nova versão de cobrança existente
    Dado o estudante 5 possui matrícula confirmada no período letivo 1
    E uma cobrança foi gerada para o estudante 5 no contrato 4
    Quando gero nova versão da cobrança com motivo "Reajuste anual" e valor 1600.00
    Então a cobrança deve estar na versão 2 com valor 1600.00
