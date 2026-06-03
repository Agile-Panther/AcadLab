Feature: Definir janelas acadêmicas do período letivo

  Scenario: Secretaria define janela de matrícula com datas válidas
    Given um período letivo cadastrado sem janelas definidas
    When a secretaria define a janela de matrícula com datas válidas
    Then a janela de matrícula é registrada no período letivo

  Scenario: Secretaria redefine janela de matrícula substituindo a anterior
    Given um período letivo cadastrado sem janelas definidas
    When a secretaria redefine a janela de matrícula com novas datas
    Then apenas uma janela de matrícula existe no período letivo
