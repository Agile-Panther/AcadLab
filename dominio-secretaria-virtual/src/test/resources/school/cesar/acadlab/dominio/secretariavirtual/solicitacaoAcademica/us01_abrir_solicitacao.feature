Feature: Abrir e submeter solicitação acadêmica

  Scenario: Estudante abre solicitação dentro do prazo com documentos obrigatórios
    Given um estudante sem solicitação aberta do tipo "APROVEITAMENTO_DISCIPLINA"
    And os documentos obrigatórios estão anexados
    And o prazo do calendário acadêmico está vigente
    When o estudante abre a solicitação acadêmica
    Then o sistema registra a solicitação com sucesso
    And a solicitação é criada com status "PENDENTE_ANALISE"
    And um protocolo é gerado para a solicitação

  Scenario: Estudante tenta abrir solicitação fora do prazo do calendário
    Given um estudante sem solicitação aberta do tipo "TRANCAMENTO_DISCIPLINA"
    And o prazo do calendário acadêmico está encerrado
    When o estudante abre a solicitação acadêmica
    Then o sistema rejeita a abertura por prazo expirado

  Scenario: Estudante tenta abrir solicitação duplicada do mesmo tipo no período
    Given um estudante com solicitação aberta do tipo "TRANCAMENTO_DISCIPLINA"
    And o prazo do calendário acadêmico está vigente
    When o estudante abre a solicitação acadêmica
    Then o sistema rejeita a abertura por duplicidade

  Scenario: Estudante abre múltiplas solicitações de revisão de nota no período
    Given um estudante com solicitação aberta do tipo "REVISAO_DE_NOTA"
    And o prazo do calendário acadêmico está vigente
    When o estudante abre outra solicitação de revisão de nota
    Then o sistema registra a solicitação com sucesso

  Scenario: Estudante tenta abrir solicitação sem documentos obrigatórios
    Given um estudante sem solicitação aberta do tipo "APROVEITAMENTO_DISCIPLINA"
    And os documentos obrigatórios não estão anexados
    And o prazo do calendário acadêmico está vigente
    When o estudante abre a solicitação acadêmica
    Then o sistema rejeita a abertura por documentação incompleta
