# language: pt

Funcionalidade: Candidatar-se a uma oportunidade de estágio

  Cenário: Estudante se candidata a oportunidade publicada com sucesso
    Dado uma oportunidade publicada
    Quando o estudante se candidata à oportunidade
    Então a candidatura é registrada com sucesso

  Cenário: Candidatura rejeitada quando oportunidade não está publicada
    Dado uma oportunidade ainda não publicada
    Quando o estudante tenta se candidatar à oportunidade
    Então o sistema deve rejeitar informando "oportunidade não está disponível para candidaturas"

  Cenário: Candidatura rejeitada quando prazo de inscrição encerrou
    Dado uma oportunidade publicada com prazo de inscrição encerrado
    Quando o estudante tenta se candidatar à oportunidade fora do prazo
    Então o sistema deve rejeitar informando "prazo de inscrição encerrado"

  Cenário: Candidatura rejeitada quando estudante não atende aos critérios de elegibilidade
    Dado uma oportunidade publicada com critério de elegibilidade exigindo curso "Sistemas de Informação"
    Quando estudante que não atende aos critérios tenta se candidatar
    Então o sistema deve rejeitar informando "estudante não atende aos critérios de elegibilidade da oportunidade"
