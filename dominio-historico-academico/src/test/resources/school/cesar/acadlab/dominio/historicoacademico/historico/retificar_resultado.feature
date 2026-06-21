# language: pt

Funcionalidade: Retificar resultado consolidado no histórico

  Cenário: Retificação preserva situação anterior e registra nova situação
    Dado um histórico com registro de disciplina consolidado para retificação
    Quando a secretaria retifica o registro para situação "APROVADO"
    Então a situação do registro é atualizada para "APROVADO"
    E a retificação preserva a situação anterior no histórico de retificações

  Cenário: Retificação de registro inexistente é rejeitada
    Dado um histórico com registro de disciplina consolidado para retificação
    Quando a secretaria tenta retificar um registro inexistente
    Então o sistema deve rejeitar informando "registro não encontrado no histórico"
