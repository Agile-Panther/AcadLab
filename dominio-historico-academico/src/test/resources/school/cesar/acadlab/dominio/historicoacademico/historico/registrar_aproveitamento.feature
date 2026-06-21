# language: pt

Funcionalidade: Registrar aproveitamento de disciplinas externas

  Cenário: Aproveitamento com carga horária compatível é registrado com sucesso
    Dado um histórico de estudante para registro de aproveitamento
    Quando a secretaria registra aproveitamento com carga horária externa igual à requerida
    Então o aproveitamento é adicionado ao histórico

  Cenário: Aproveitamento com carga horária insuficiente é rejeitado
    Dado um histórico de estudante para registro de aproveitamento
    Quando a secretaria tenta registrar aproveitamento com carga horária insuficiente
    Então o sistema deve rejeitar informando "carga horária externa insuficiente para aproveitamento"
