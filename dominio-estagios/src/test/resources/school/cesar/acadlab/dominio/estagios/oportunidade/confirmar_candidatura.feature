Feature: Confirmar ou recusar candidatura de estágio

  Scenario: Empresa confirma candidatura e estágio é criado
    Given uma oportunidade encaminhada com candidato de id 20
    When a empresa de id 10 confirma a candidatura
    Then o estágio é criado com status EM_ANDAMENTO
    And o estágio possui o estudante de id 20

  Scenario: Empresa recusa candidatura
    Given uma oportunidade encaminhada com candidato de id 20
    When a empresa de id 10 recusa a candidatura
    Then a oportunidade fica com status RECUSADA

  Scenario: Confirmação rejeitada sem encaminhamento prévio
    Given uma oportunidade aberta com candidato de id 20
    When a empresa tenta confirmar sem encaminhamento
    Then o sistema rejeita a confirmação com mensagem sobre RN-5
