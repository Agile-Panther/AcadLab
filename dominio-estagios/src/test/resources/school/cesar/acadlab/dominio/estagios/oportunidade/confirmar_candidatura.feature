# language: pt

Funcionalidade: Deferir e confirmar candidatura

  Cenário: Setor de estágios defere candidatura e empresa confirma gerando estágio
    Dado uma candidatura em análise para o estudante de id 20
    Quando o setor de estágios defere a candidatura
    E a empresa confirma a candidatura deferida
    Então o estágio é criado com status EM_ANDAMENTO

  Cenário: Empresa tenta confirmar candidatura que ainda não foi deferida
    Dado uma candidatura em análise para o estudante de id 20
    Quando a empresa tenta confirmar candidatura não deferida
    Então o sistema deve rejeitar informando "candidatura precisa estar deferida para gerar encaminhamento"

  Cenário: Setor de estágios defere candidatura com sucesso
    Dado uma candidatura em análise para o estudante de id 20
    Quando o setor de estágios defere a candidatura
    Então a candidatura possui status DEFERIDA
