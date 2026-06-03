Feature: Ofertar turma de uma disciplina

  Scenario: Coordenação oferta turma com dados válidos
    Given um período letivo e uma disciplina disponíveis
    When a coordenação oferta uma turma para a disciplina
    Then a turma é criada com status planejada

  Scenario: Coordenação configura turma completa e confirma oferta
    Given um período letivo e uma disciplina disponíveis
    When a coordenação vincula professor, sala e horário à turma e confirma a oferta
    Then a turma é ofertada com status ofertada

  Scenario: Coordenação vincula sala com capacidade insuficiente à turma
    Given um período letivo e uma disciplina disponíveis
    When a coordenação vincula uma sala com capacidade insuficiente à turma
    Then o sistema rejeita a vinculação informando capacidade insuficiente
