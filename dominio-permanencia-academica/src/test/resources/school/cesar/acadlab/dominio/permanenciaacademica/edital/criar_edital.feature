Feature: Criar edital de permanência acadêmica

  Scenario: Criar edital com sucesso para programa sem edital aberto
    Given não existe edital com inscrições abertas para o programa "Bolsa Permanência"
    When a secretaria cria um edital para o programa "Bolsa Permanência" com 5 vagas
    Then o sistema registra o edital com status de inscrições abertas

  Scenario: Criar edital falha quando já existe edital aberto para o mesmo programa
    Given existe um edital com inscrições abertas para o programa "Auxílio Transporte"
    When a secretaria tenta criar um novo edital para o programa "Auxílio Transporte" com 3 vagas
    Then o sistema informa que já existe um edital com inscrições abertas para o programa
