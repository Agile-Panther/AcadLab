Feature: Encerrar estágio

  Scenario: Estudante solicita encerramento e coordenador homologa
    Given um estágio em andamento para o estudante de id 20
    When o estudante solicita o encerramento do estágio
    Then o estágio possui status ENCERRAMENTO_SOLICITADO
    When o coordenador de id 30 homologa o encerramento
    Then o estágio possui status ENCERRADO

  Scenario: Solicitação de encerramento rejeitada quando não está em andamento
    Given um estágio com encerramento já solicitado
    When o estudante tenta solicitar encerramento novamente
    Then o sistema rejeita o encerramento com mensagem sobre RN-11

  Scenario: Homologação rejeitada sem solicitação prévia
    Given um estágio em andamento para o estudante de id 20
    When o coordenador de id 30 tenta homologar sem solicitação
    Then o sistema rejeita a homologação com mensagem sobre RN-12
