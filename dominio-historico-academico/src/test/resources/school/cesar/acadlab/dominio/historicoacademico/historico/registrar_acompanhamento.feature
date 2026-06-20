# language: pt

Funcionalidade: Registrar acompanhamento acadêmico

  Cenário: Coordenador registra acompanhamento para estudante com matrícula ativa
    Dado um histórico de estudante para acompanhamento acadêmico
    Quando o coordenador registra acompanhamento para estudante com vínculo ativo
    Então o acompanhamento é adicionado ao histórico

  Cenário: Acompanhamento de estudante sem vínculo ativo é rejeitado
    Dado um histórico de estudante para acompanhamento acadêmico
    Quando o coordenador tenta registrar acompanhamento para estudante sem vínculo
    Então o sistema deve rejeitar informando "estudante não possui vínculo ativo"
