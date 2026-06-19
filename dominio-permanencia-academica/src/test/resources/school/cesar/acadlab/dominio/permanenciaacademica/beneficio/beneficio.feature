# language: pt

Funcionalidade: Gerenciar benefício de permanência acadêmica

  Cenário: Estudante solicita renovação dentro do prazo
    Dado um estudante possui um benefício ativo com prazo de renovação futuro
    Quando o estudante solicita a renovação do benefício
    Então o sistema registra a solicitação de renovação

  Cenário: Renovação falha quando fora do prazo
    Dado um estudante possui um benefício ativo com prazo de renovação já vencido
    Quando o estudante tenta solicitar a renovação do benefício
    Então o sistema deve rejeitar informando "prazo de renovação já encerrou"

  Cenário: Benefício suspenso por não cumprimento dos critérios mínimos
    Dado um estudante possui um benefício ativo
    Quando o sistema suspende o benefício por não cumprimento dos critérios
    Então o status do benefício é atualizado para suspenso
    E um evento de suspensão é publicado no barramento

  Cenário: Benefício cancelado por não cumprimento dos critérios mínimos
    Dado um estudante possui um benefício ativo
    Quando o sistema cancela o benefício por não cumprimento dos critérios
    Então o status do benefício é atualizado para cancelado
    E um evento de cancelamento é publicado no barramento
