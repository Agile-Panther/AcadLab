Feature: Submeter Atividade Complementar

  Scenario: Submeter atividade com vínculo ativo e certificado único
    Given um estudante com vínculo ativo no período de realização da atividade
    And o certificado "CERT-001" ainda não foi utilizado
    When o estudante submete a atividade da categoria 1 com 40 horas realizada em "15/03/2025" com certificado "CERT-001"
    Then a atividade deve ser salva com status PENDENTE

  Scenario: Rejeitar atividade realizada fora do período de vínculo
    Given um estudante sem vínculo ativo na data de realização
    When o estudante tenta submeter a atividade da categoria 1 com 40 horas realizada em "15/03/2025" com certificado "CERT-002"
    Then deve ser lançada uma exceção de vínculo inativo

  Scenario: Rejeitar atividade com certificado já utilizado
    Given um estudante com vínculo ativo no período de realização da atividade
    And o certificado "CERT-003" já foi utilizado anteriormente
    When o estudante tenta submeter a atividade da categoria 1 com 40 horas realizada em "15/03/2025" com certificado "CERT-003"
    Then deve ser lançada uma exceção de certificado duplicado
