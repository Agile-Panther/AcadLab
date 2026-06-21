# language: pt

Funcionalidade: Cancelar período letivo

  Cenário: Secretaria cancela período letivo não iniciado sem matrículas
    Dado um período letivo cadastrado passível de cancelamento
    E sem matrículas confirmadas no período
    Quando a secretaria cancela o período letivo
    Então o período letivo deve ter status cancelado

  Cenário: Sistema rejeita cancelamento com matrículas confirmadas
    Dado um período letivo cadastrado passível de cancelamento
    E com matrículas confirmadas que impedem o cancelamento
    Quando a secretaria tenta cancelar o período letivo
    Então o sistema deve rejeitar informando "período letivo possui matrículas confirmadas e não pode ser cancelado"

  Cenário: Sistema rejeita cancelamento de período já encerrado
    Dado um período letivo já encerrado para tentativa de cancelamento
    Quando a secretaria tenta cancelar o período letivo
    Então o sistema deve rejeitar informando "período letivo encerrado não pode ser cancelado"

  Cenário: Sistema rejeita cancelamento de período em andamento
    Dado um período letivo em andamento para tentativa de cancelamento
    Quando a secretaria tenta cancelar o período letivo
    Então o sistema deve rejeitar informando "período letivo em andamento não pode ser cancelado"
