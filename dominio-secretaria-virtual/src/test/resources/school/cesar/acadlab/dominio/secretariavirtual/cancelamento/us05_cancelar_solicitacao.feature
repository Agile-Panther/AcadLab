# language: pt

Funcionalidade: Cancelar solicitação acadêmica aberta

  Cenário: Estudante cancela solicitação pendente de análise
    Dado uma solicitação com status pendente de análise para cancelamento
    Quando o estudante cancela a solicitação
    Então a solicitação é cancelada com sucesso

  Cenário: Estudante tenta cancelar solicitação em análise
    Dado uma solicitação com status em análise para cancelamento
    Quando o estudante tenta cancelar a solicitação
    Então o sistema deve rejeitar informando "solicitação em análise não pode ser cancelada"

  Cenário: Estudante tenta cancelar solicitação deferida
    Dado uma solicitação com status deferida para cancelamento
    Quando o estudante tenta cancelar a solicitação
    Então o sistema deve rejeitar informando "solicitação deferida não pode ser cancelada"
