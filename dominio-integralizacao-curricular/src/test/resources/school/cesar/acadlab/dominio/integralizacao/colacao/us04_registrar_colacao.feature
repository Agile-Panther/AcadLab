# language: pt

Funcionalidade: Registrar cerimônia de colação de grau

  Cenário: Secretaria registra colação para estudante com aptidão aprovada
    Dado um estudante com aptidão formalmente aprovada
    Quando a secretaria registra a colação de grau com data válida
    Então a colação de grau é registrada com sucesso

  Cenário: Secretaria tenta registrar colação sem aptidão aprovada
    Dado um estudante sem aptidão aprovada
    Quando a secretaria tenta registrar colação sem aptidão aprovada
    Então o sistema deve rejeitar informando "estudante não possui aptidão aprovada para colação de grau"
