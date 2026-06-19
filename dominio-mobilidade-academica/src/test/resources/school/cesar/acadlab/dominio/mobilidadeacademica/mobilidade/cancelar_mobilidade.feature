#language: pt
Funcionalidade: Cancelar mobilidade acadêmica

  Cenário: Estudante cancela mobilidade antes do início do período externo
    Dado uma mobilidade solicitada para estudante id 8 sem período iniciado
    Quando o estudante solicita cancelamento com justificativa "Motivo pessoal" em "2025-01-10"
    E o coordenador confirma o cancelamento da mobilidade
    Então a mobilidade tem status CANCELADA

  Cenário: Cancelar mobilidade após o período já iniciado é rejeitado
    Dado uma mobilidade em andamento para estudante id 9 iniciada em "2025-03-01"
    Quando o estudante tenta cancelar a mobilidade em andamento em "2025-03-15"
    Então o sistema rejeita o cancelamento com mensagem sobre RN-7

  Cenário: Confirmar cancelamento sem justificativa prévia é rejeitado
    Dado uma mobilidade solicitada para estudante id 10 sem justificativa de cancelamento
    Quando o coordenador tenta confirmar cancelamento sem justificativa prévia
    Então o sistema rejeita a confirmação com mensagem sobre RN-8
