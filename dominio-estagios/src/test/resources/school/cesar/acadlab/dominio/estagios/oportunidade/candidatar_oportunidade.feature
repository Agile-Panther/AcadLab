#language: pt
Funcionalidade: Candidatar-se a uma oportunidade de estágio

  Cenário: Estudante se candidata a oportunidade aberta com sucesso
    Dado uma oportunidade de estágio aberta para a empresa de id 10 com descrição "Estágio em TI" e carga horária 480
    Quando o estudante de id 20 se candidata à oportunidade
    Então a oportunidade possui candidato com id 20

  Cenário: Candidatura rejeitada quando oportunidade não está aberta
    Dado uma oportunidade de estágio já encaminhada
    Quando o estudante de id 20 tenta se candidatar à oportunidade encaminhada
    Então o sistema rejeita a candidatura com mensagem sobre RN-1

  Cenário: Candidatura rejeitada quando oportunidade já possui candidato
    Dado uma oportunidade de estágio aberta com candidato de id 20
    Quando o estudante de id 99 tenta se candidatar à oportunidade com candidato
    Então o sistema rejeita a candidatura com mensagem sobre RN-2
