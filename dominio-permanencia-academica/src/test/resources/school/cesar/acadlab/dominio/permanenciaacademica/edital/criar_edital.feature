# language: pt

Funcionalidade: Criar edital de permanência acadêmica

  Cenário: Criar edital com sucesso para programa sem edital aberto
    Dado não existe edital com inscrições abertas para o programa "Bolsa Permanência"
    Quando a secretaria cria um edital para o programa "Bolsa Permanência" com 5 vagas
    Então o sistema registra o edital com status de inscrições abertas

  Cenário: Criar edital falha quando já existe edital aberto para o mesmo programa
    Dado existe um edital com inscrições abertas para o programa "Auxílio Transporte"
    Quando a secretaria tenta criar um novo edital para o programa "Auxílio Transporte" com 3 vagas
    Então o sistema deve rejeitar informando "já existe edital com inscrições abertas para este programa"
