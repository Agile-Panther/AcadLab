# language: pt

Funcionalidade: Analisar Atividade Complementar

  Cenário: Deferir atividade dentro do limite da categoria
    Dado uma atividade complementar pendente cadastrada para análise
    E o deferimento não excede o limite da categoria
    Quando o coordenador defere a atividade com 30 horas aprovadas
    Então a atividade deve ter status DEFERIDA com 30 horas aprovadas

  Cenário: Rejeitar deferimento que excede o limite da categoria
    Dado uma atividade complementar pendente cadastrada para análise
    E o deferimento excede o limite da categoria
    Quando o coordenador tenta deferir a atividade com 30 horas
    Então o sistema deve rejeitar informando "horas aprovadas excedem o limite da categoria"

  Cenário: Indeferir atividade complementar
    Dado uma atividade complementar pendente cadastrada para análise
    Quando o coordenador indefere a atividade com justificativa "Certificado inválido"
    Então a atividade deve ter status INDEFERIDA

  Cenário: Deferir atividade em revisão solicitada
    Dado uma atividade complementar em revisão solicitada
    E o deferimento não excede o limite da categoria
    Quando o coordenador defere a atividade com 25 horas aprovadas
    Então a atividade deve ter status DEFERIDA com 25 horas aprovadas

  Cenário: Indeferir atividade em revisão solicitada é rejeitado ao ser deferida
    Dado uma atividade complementar em revisão solicitada
    Quando o coordenador tenta indeferir a atividade com justificativa "Ainda insuficiente"
    Então a atividade deve ter status INDEFERIDA
