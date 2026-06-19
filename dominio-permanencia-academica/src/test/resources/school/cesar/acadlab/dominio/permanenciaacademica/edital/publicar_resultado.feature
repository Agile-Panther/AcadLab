#language: pt
Funcionalidade: Publicar resultado e encerrar edital

  Cenário: Resultado publicado com sucesso após prazo de recursos encerrado
    Dado existe um edital com inscrições encerradas e prazo de recurso expirado
    Quando a assistência estudantil publica o resultado final
    Então o sistema atualiza o status do edital para resultado publicado

  Cenário: Publicação falha quando prazo de recursos ainda não encerrou
    Dado existe um edital com inscrições encerradas e prazo de recurso ainda aberto
    Quando a assistência estudantil tenta publicar o resultado final
    Então o sistema informa que o prazo de recursos ainda não encerrou

  Cenário: Edital encerrado com sucesso após publicação do resultado
    Dado existe um edital com resultado final publicado
    Quando a secretaria encerra o edital
    Então o sistema atualiza o status do edital para encerrado

  Cenário: Encerramento falha quando resultado ainda não foi publicado
    Dado existe um edital com inscrições abertas
    Quando a secretaria tenta encerrar o edital
    Então o sistema informa que o resultado final ainda não foi publicado
