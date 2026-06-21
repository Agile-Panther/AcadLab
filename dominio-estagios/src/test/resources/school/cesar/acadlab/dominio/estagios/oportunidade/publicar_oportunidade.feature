# language: pt

Funcionalidade: Publicar oportunidade de estágio

  Cenário: Setor de estágios publica oportunidade cadastrada com sucesso
    Dado uma oportunidade cadastrada pela empresa
    Quando o setor de estágios publica a oportunidade
    Então a oportunidade fica visível para os estudantes

  Cenário: Tentativa de publicar oportunidade já publicada
    Dado uma oportunidade cadastrada pela empresa
    Quando o setor de estágios publica a oportunidade
    E o setor tenta publicar a oportunidade novamente
    Então o sistema deve rejeitar informando "publicação só pode ser realizada em oportunidades cadastradas"
