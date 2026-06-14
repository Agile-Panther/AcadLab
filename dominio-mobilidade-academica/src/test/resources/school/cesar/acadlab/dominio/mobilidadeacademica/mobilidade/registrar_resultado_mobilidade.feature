Feature: Registrar resultado de mobilidade

  Scenario: Secretaria registra resultado com comprovante anexado
    Given uma mobilidade com item autorizado e comprovante anexado para estudante id 6
    When a secretaria com id 1 registra o resultado da disciplina externa 10
    Then o resultado da disciplina externa 10 é registrado com sucesso

  Scenario: Registro de resultado sem comprovante é rejeitado
    Given uma mobilidade com item autorizado sem comprovante para estudante id 7
    When a secretaria com id 1 tenta registrar resultado da disciplina 10 sem comprovante
    Then o sistema rejeita o registro com mensagem sobre RN-4
