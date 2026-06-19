#language: pt
Funcionalidade: Consultar histórico de casos psicopedagógicos

  Cenário: Psicopedagogo consulta seus próprios casos
    Dado um psicopedagogo com casos atribuídos a ele
    Quando o psicopedagogo consulta o histórico de casos
    Então o sistema retorna apenas os casos nos quais o profissional é responsável

  Cenário: Psicopedagogo não vê casos de outro profissional
    Dado um psicopedagogo com casos atribuídos a outro profissional
    Quando o psicopedagogo consulta o histórico de casos
    Então o sistema retorna uma lista vazia de casos
