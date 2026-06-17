Feature: Candidatar-se a uma oportunidade de estágio

  Scenario: Estudante se candidata a oportunidade aberta com sucesso
    Given uma oportunidade de estágio aberta para a empresa de id 10 com descrição "Estágio em TI" e carga horária 480
    When o estudante de id 20 se candidata à oportunidade
    Then a oportunidade possui candidato com id 20

  Scenario: Candidatura rejeitada quando oportunidade não está aberta
    Given uma oportunidade de estágio já encaminhada
    When o estudante de id 20 tenta se candidatar à oportunidade encaminhada
    Then o sistema rejeita a candidatura com mensagem sobre RN-1

  Scenario: Candidatura rejeitada quando oportunidade já possui candidato
    Given uma oportunidade de estágio aberta com candidato de id 20
    When o estudante de id 99 tenta se candidatar à oportunidade com candidato
    Then o sistema rejeita a candidatura com mensagem sobre RN-2
