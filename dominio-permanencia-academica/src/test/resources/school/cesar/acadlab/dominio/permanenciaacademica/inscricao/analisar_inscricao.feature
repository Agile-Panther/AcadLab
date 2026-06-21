# language: pt

Funcionalidade: Analisar inscrições e interpor recurso

  Cenário: Assistência estudantil defere inscrição com sucesso
    Dado existe uma inscrição pendente no edital
    Quando a assistência estudantil defere a inscrição com pontuação 80
    Então o sistema atualiza o status da inscrição para deferida

  Cenário: Assistência estudantil indefere inscrição com sucesso
    Dado existe uma inscrição pendente no edital
    Quando a assistência estudantil indefere a inscrição
    Então o sistema atualiza o status da inscrição para indeferida

  Cenário: Estudante interpõe recurso contra indeferimento dentro do prazo
    Dado existe uma inscrição indeferida e o prazo de recurso está aberto
    Quando o estudante interpõe recurso contra o indeferimento
    Então o sistema registra o recurso e atualiza o status para recurso interposto

  Cenário: Recurso falha quando já foi interposto anteriormente
    Dado existe uma inscrição com recurso já interposto
    Quando o estudante tenta interpor novo recurso
    Então o sistema deve rejeitar informando "já foi interposto um recurso para esta inscrição"
