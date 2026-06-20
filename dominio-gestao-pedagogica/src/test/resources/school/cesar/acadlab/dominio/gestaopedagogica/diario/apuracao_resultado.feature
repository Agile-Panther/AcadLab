# language: pt

Funcionalidade: Apuração do resultado por regime (Template Method)

  Os regimes de apuração compartilham o mesmo algoritmo de fechamento (frequência,
  cálculo da média e situação final), variando apenas a forma de calcular a média.
  Para a mesma entrada, regimes diferentes podem produzir situações diferentes.

  Cenário: Regime de média ponderada coloca o estudante em recuperação
    Dado um diário com avaliações de pesos 80 e 20 e o estudante com notas 5 e 9
    Quando o professor fecha o resultado pelo regime "PONDERADA"
    Então a situação final do estudante é "RECUPERACAO"

  Cenário: Regime de média aritmética aprova o mesmo estudante
    Dado um diário com avaliações de pesos 80 e 20 e o estudante com notas 5 e 9
    Quando o professor fecha o resultado pelo regime "ARITMETICA"
    Então a situação final do estudante é "APROVADO"
