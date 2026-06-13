Feature: Solicitar Revisão de Atividade Complementar

  Scenario: Solicitar revisão de atividade indeferida não contabilizada
    Given uma atividade complementar no estado indeferida
    And a atividade não foi contabilizada na integralização curricular
    When o estudante solicita revisão com justificativa "Enviei documentação complementar"
    Then a atividade deve ter status REVISAO_SOLICITADA

  Scenario: Rejeitar revisão de atividade já contabilizada
    Given uma atividade complementar no estado indeferida
    And a atividade já foi contabilizada na integralização curricular
    When o estudante tenta solicitar revisão da atividade
    Then deve ser lançada uma exceção de atividade já contabilizada
