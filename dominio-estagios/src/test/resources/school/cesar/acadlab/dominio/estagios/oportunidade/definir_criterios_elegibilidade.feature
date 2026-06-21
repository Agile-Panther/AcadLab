# language: pt

Funcionalidade: Definir critérios de elegibilidade da oportunidade

  Cenário: Setor define critérios antes da publicação com sucesso
    Dado uma oportunidade cadastrada pronta para receber critérios
    Quando o setor de estágios define os critérios de elegibilidade
    Então os critérios são registrados com sucesso

  Cenário: Tentativa de alterar critérios após publicação
    Dado uma oportunidade publicada com critérios definidos
    Quando o setor tenta alterar os critérios de elegibilidade
    Então o sistema deve rejeitar informando "critérios não podem ser alterados após a publicação"
