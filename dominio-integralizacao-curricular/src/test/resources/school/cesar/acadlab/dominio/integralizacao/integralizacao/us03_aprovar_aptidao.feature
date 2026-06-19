# language: pt

Funcionalidade: Aprovar aptidão para colação de grau

  Cenário: Coordenador aprova aptidão de estudante que cumpriu todos os requisitos
    Dado uma integralização com resultado apto e requisitos cumpridos
    Quando o coordenador aprova a aptidão para colação de grau
    Então a aptidão é formalmente aprovada com registro do coordenador

  Cenário: Coordenador tenta aprovar aptidão sem cumprimento dos requisitos
    Dado uma integralização com resultado apto mas requisitos não cumpridos
    Quando o coordenador tenta aprovar a aptidão para colação de grau
    Então o sistema deve rejeitar informando "requisitos curriculares não foram cumpridos"

  Cenário: Tentativa de aprovar aptidão com resultado inapto
    Dado uma integralização com resultado inapto
    Quando o coordenador tenta aprovar a aptidão para colação de grau
    Então o sistema deve rejeitar informando "resultado da análise de integralização é inapto"
