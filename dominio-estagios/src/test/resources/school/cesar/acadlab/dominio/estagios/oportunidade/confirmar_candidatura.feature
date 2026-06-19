#language: pt
Funcionalidade: Confirmar ou recusar candidatura de estágio

  Cenário: Empresa confirma candidatura e estágio é criado
    Dado uma oportunidade encaminhada com candidato de id 20
    Quando a empresa de id 10 confirma a candidatura
    Então o estágio é criado com status EM_ANDAMENTO
    E o estágio possui o estudante de id 20

  Cenário: Empresa recusa candidatura
    Dado uma oportunidade encaminhada com candidato de id 20
    Quando a empresa de id 10 recusa a candidatura
    Então a oportunidade fica com status RECUSADA

  Cenário: Confirmação rejeitada sem encaminhamento prévio
    Dado uma oportunidade aberta com candidato de id 20
    Quando a empresa tenta confirmar sem encaminhamento
    Então o sistema rejeita a confirmação com mensagem sobre RN-5
