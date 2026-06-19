# language: pt

Funcionalidade: Editar período letivo

  Cenário: Secretaria edita datas de período não iniciado
    Dado um período letivo cadastrado editável
    Quando a secretaria edita o período letivo para as datas de "15/03/2028" a "30/07/2028"
    Então o período letivo deve ter data de início "15/03/2028" e data de fim "30/07/2028"

  Cenário: Sistema rejeita edição de período já encerrado
    Dado um período letivo com status encerrado
    Quando a secretaria tenta editar o período letivo para as datas de "15/03/2028" a "30/07/2028"
    Então o sistema deve rejeitar informando "período letivo encerrado não pode ser editado"

  Cenário: Sistema rejeita edição de período em andamento
    Dado um período letivo em andamento para tentativa de edição
    Quando a secretaria tenta editar o período letivo para as datas de "15/03/2028" a "30/07/2028"
    Então o sistema deve rejeitar informando "período letivo em andamento não pode ser editado"
