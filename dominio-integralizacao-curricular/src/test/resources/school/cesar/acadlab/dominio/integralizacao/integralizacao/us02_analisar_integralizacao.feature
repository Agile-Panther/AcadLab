# language: pt

Funcionalidade: Analisar integralização curricular e registrar resultado

  Cenário: Secretaria analisa integralização com todos os requisitos cumpridos e registra resultado apto
    Dado uma solicitação de análise de integralização iniciada
    Quando a secretaria gera o checklist com todos os requisitos cumpridos
    Então a integralização é registrada com resultado apto

  Cenário: Secretaria analisa integralização com pendências e registra resultado inapto
    Dado uma solicitação de análise de integralização iniciada
    Quando a secretaria gera o checklist com pendências e registra resultado inapto
    Então a integralização é registrada com resultado inapto

  Cenário: Coordenador aprova aptidão do estudante com resultado apto
    Dado uma solicitação de análise de integralização iniciada
    Quando a secretaria gera o checklist com todos os requisitos cumpridos
    E o coordenador aprova a aptidão do estudante
    Então a aptidão do estudante é formalmente aprovada
