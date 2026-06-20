# language: pt

Funcionalidade: Cancelar Submissão de Atividade Complementar

  Cenário: Cancelar atividade com status pendente de análise
    Dado uma atividade complementar com status pendente aguardando cancelamento
    Quando o estudante solicita o cancelamento da submissão
    Então a atividade deve ter status CANCELADA

  Cenário: Rejeitar cancelamento de atividade já analisada
    Dado uma atividade complementar com status deferida aguardando cancelamento
    Quando o estudante tenta solicitar o cancelamento da submissão
    Então o sistema deve rejeitar informando "atividade já analisada não pode ser cancelada"
