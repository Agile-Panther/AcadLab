#language: pt
Funcionalidade: Submeter relatório de estágio

  Cenário: Estudante submete relatório com sucesso
    Dado um estágio ativo para o estudante de id 20
    Quando o estudante submete o relatório número 1 com descrição "Relatório mensal de atividades"
    Então o estágio possui 1 relatório com status PENDENTE

  Cenário: Submissão rejeitada com número duplicado
    Dado um estágio com relatório número 1 já submetido
    Quando o estudante tenta submeter novamente o relatório número 1
    Então o sistema rejeita a submissão com mensagem sobre RN-8

  Cenário: Coordenador aprova relatório
    Dado um estágio com relatório número 1 pendente
    Quando o coordenador aprova o relatório número 1
    Então o relatório número 1 possui status APROVADO
