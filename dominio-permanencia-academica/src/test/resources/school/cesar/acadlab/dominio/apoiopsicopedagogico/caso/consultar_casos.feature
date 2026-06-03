Feature: Consultar histórico de casos psicopedagógicos

  Scenario: Psicopedagogo consulta seus próprios casos
    Given um psicopedagogo com casos atribuídos a ele
    When o psicopedagogo consulta o histórico de casos
    Then o sistema retorna apenas os casos nos quais o profissional é responsável

  Scenario: Psicopedagogo não vê casos de outro profissional
    Given um psicopedagogo com casos atribuídos a outro profissional
    When o psicopedagogo consulta o histórico de casos
    Then o sistema retorna uma lista vazia de casos
