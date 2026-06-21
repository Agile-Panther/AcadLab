# language: pt

Funcionalidade: Analisar solicitações acadêmicas

  Cenário: Secretaria defere solicitação sem impacto acadêmico e conclui
    Dado uma solicitação pendente de análise
    Quando a secretaria inicia a análise da solicitação
    E a secretaria defere a solicitação sem impacto acadêmico
    E a secretaria conclui a solicitação
    Então a solicitação é concluída com sucesso

  Cenário: Secretaria defere solicitação com impacto acadêmico sem vincular alterações
    Dado uma solicitação pendente de análise
    Quando a secretaria inicia a análise da solicitação
    E a secretaria defere a solicitação com impacto acadêmico
    E a secretaria tenta concluir a solicitação
    Então o sistema deve rejeitar informando "solicitação deferida com impacto acadêmico requer vinculação de alterações"

  Cenário: Secretaria defere solicitação com impacto e vincula alterações antes de concluir
    Dado uma solicitação pendente de análise
    Quando a secretaria inicia a análise da solicitação
    E a secretaria defere a solicitação com impacto acadêmico
    E as alterações são vinculadas ao protocolo
    E a secretaria conclui a solicitação
    Então a solicitação é concluída com sucesso

  Cenário: Secretaria indefere solicitação com justificativa
    Dado uma solicitação pendente de análise
    Quando a secretaria inicia a análise da solicitação
    E a secretaria indefere a solicitação
    Então a solicitação é indeferida com justificativa registrada
