# language: pt

Funcionalidade: Aprovar aptidão para colação de grau
  Como coordenador acadêmico
  Quero aprovar formalmente a aptidão de um estudante para colação de grau
  Para liberar oficialmente a conclusão do curso

  Cenário: Coordenador aprova aptidão de estudante que cumpriu todos os requisitos
    Dado uma integralização com resultado apto e requisitos cumpridos
    Quando o coordenador aprova a aptidão para colação de grau
    Então a aptidão é formalmente aprovada com registro do coordenador

  Cenário: Coordenador tenta aprovar aptidão sem cumprimento dos requisitos
    Dado uma integralização com resultado apto mas requisitos não cumpridos
    Quando o coordenador tenta aprovar a aptidão para colação de grau
    Então o sistema rejeita a aprovação por requisitos não cumpridos

  Cenário: Tentativa de aprovar aptidão com resultado inapto
    Dado uma integralização com resultado inapto
    Quando o coordenador tenta aprovar a aptidão para colação de grau
    Então o sistema rejeita a aprovação por resultado não apto
