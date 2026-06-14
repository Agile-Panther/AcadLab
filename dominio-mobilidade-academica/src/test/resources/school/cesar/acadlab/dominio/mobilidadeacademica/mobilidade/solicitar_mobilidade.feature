Feature: Solicitar mobilidade acadêmica

  Scenario: Estudante solicita mobilidade e coordenador autoriza
    Given um estudante com id 1 deseja mobilidade para "MIT"
    When o estudante solicita a mobilidade acadêmica
    Then a mobilidade é registrada com status SOLICITADA
    When o coordenador com id 1 autoriza a mobilidade
    Then a mobilidade tem status AUTORIZADA

  Scenario: Autorizar uma mobilidade já autorizada é rejeitado
    Given uma mobilidade académica já autorizada para o estudante com id 2
    When o coordenador com id 1 tenta autorizar a mobilidade já autorizada
    Then o sistema rejeita a autorização com mensagem sobre RN-1
