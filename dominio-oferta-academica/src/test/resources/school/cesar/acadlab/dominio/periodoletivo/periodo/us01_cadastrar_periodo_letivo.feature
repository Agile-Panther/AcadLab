Feature: Cadastrar período letivo

  Scenario: Secretaria cadastra período letivo com datas válidas sem sobreposição
    Given que não existe período letivo cadastrado para o curso
    When a secretaria cadastra um novo período letivo com datas válidas
    Then o período letivo é cadastrado com status não iniciado

  Scenario: Secretaria tenta cadastrar período com datas sobrepostas ao período existente
    Given que já existe um período letivo com datas sobrepostas para o mesmo curso
    When a secretaria tenta cadastrar um período letivo com datas sobrepostas
    Then o sistema rejeita o cadastro informando sobreposição de datas
