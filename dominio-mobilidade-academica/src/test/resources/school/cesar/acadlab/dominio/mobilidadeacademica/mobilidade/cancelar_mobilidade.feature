Feature: Cancelar mobilidade acadêmica

  Scenario: Estudante cancela mobilidade antes do início do período externo
    Given uma mobilidade solicitada para estudante id 8 sem período iniciado
    When o estudante solicita cancelamento com justificativa "Motivo pessoal" em 2025-01-10
    And o coordenador confirma o cancelamento da mobilidade
    Then a mobilidade tem status CANCELADA

  Scenario: Cancelar mobilidade após o período já iniciado é rejeitado
    Given uma mobilidade em andamento para estudante id 9 iniciada em 2025-03-01
    When o estudante tenta cancelar a mobilidade em andamento em 2025-03-15
    Then o sistema rejeita o cancelamento com mensagem sobre RN-7

  Scenario: Confirmar cancelamento sem justificativa prévia é rejeitado
    Given uma mobilidade solicitada para estudante id 10 sem justificativa de cancelamento
    When o coordenador tenta confirmar cancelamento sem justificativa prévia
    Then o sistema rejeita a confirmação com mensagem sobre RN-8
