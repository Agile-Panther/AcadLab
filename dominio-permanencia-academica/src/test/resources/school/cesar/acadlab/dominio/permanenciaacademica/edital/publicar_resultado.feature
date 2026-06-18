Feature: Publicar resultado e encerrar edital

  Scenario: Resultado publicado com sucesso após prazo de recursos encerrado
    Given existe um edital com inscrições encerradas e prazo de recurso expirado
    When a assistência estudantil publica o resultado final
    Then o sistema atualiza o status do edital para resultado publicado

  Scenario: Publicação falha quando prazo de recursos ainda não encerrou
    Given existe um edital com inscrições encerradas e prazo de recurso ainda aberto
    When a assistência estudantil tenta publicar o resultado final
    Then o sistema informa que o prazo de recursos ainda não encerrou

  Scenario: Edital encerrado com sucesso após publicação do resultado
    Given existe um edital com resultado final publicado
    When a secretaria encerra o edital
    Then o sistema atualiza o status do edital para encerrado

  Scenario: Encerramento falha quando resultado ainda não foi publicado
    Given existe um edital com inscrições abertas
    When a secretaria tenta encerrar o edital
    Then o sistema informa que o resultado final ainda não foi publicado
