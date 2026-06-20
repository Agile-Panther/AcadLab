# language: pt

Funcionalidade: Encerrar período letivo

  Cenário: Secretaria encerra período letivo sem pendências
    Dado um período letivo cadastrado pronto para encerramento
    E sem pendências que impedem o encerramento
    Quando a secretaria encerra o período letivo
    Então o período letivo deve ter status encerrado

  Cenário: Sistema rejeita encerramento com pendências abertas
    Dado um período letivo cadastrado pronto para encerramento
    E com pendências que impedem o encerramento
    Quando a secretaria tenta encerrar o período letivo
    Então o sistema deve rejeitar informando "período letivo possui pendências abertas que impedem o encerramento"

  Cenário: Sistema rejeita encerramento de período já encerrado
    Dado um período letivo já encerrado anteriormente
    Quando a secretaria tenta encerrar o período letivo
    Então o sistema deve rejeitar informando "período letivo já está encerrado"
