Feature: Submeter relatório de estágio

  Scenario: Estudante submete relatório com sucesso
    Given um estágio ativo para o estudante de id 20
    When o estudante submete o relatório número 1 com descrição "Relatório mensal de atividades"
    Then o estágio possui 1 relatório com status PENDENTE

  Scenario: Submissão rejeitada com número duplicado
    Given um estágio com relatório número 1 já submetido
    When o estudante tenta submeter novamente o relatório número 1
    Then o sistema rejeita a submissão com mensagem sobre RN-8

  Scenario: Coordenador aprova relatório
    Given um estágio com relatório número 1 pendente
    When o coordenador aprova o relatório número 1
    Then o relatório número 1 possui status APROVADO
