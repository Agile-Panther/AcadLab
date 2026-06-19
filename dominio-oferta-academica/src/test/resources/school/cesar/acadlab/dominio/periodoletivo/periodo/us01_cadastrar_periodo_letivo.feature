#language: pt
Funcionalidade: Cadastrar período letivo

  Cenário: Secretaria cadastra período letivo com datas válidas sem sobreposição
    Dado que não existe período letivo cadastrado para o curso
    Quando a secretaria cadastra um novo período letivo com datas válidas
    Então o período letivo é cadastrado com status não iniciado

  Cenário: Secretaria tenta cadastrar período com datas sobrepostas ao período existente
    Dado que já existe um período letivo com datas sobrepostas para o mesmo curso
    Quando a secretaria tenta cadastrar um período letivo com datas sobrepostas
    Então o sistema rejeita o cadastro informando sobreposição de datas
