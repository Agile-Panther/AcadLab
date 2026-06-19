#language: pt
Funcionalidade: Ofertar turma de uma disciplina

  Cenário: Coordenação oferta turma com dados válidos
    Dado um período letivo e uma disciplina disponíveis
    Quando a coordenação oferta uma turma para a disciplina
    Então a turma é criada com status planejada

  Cenário: Coordenação configura turma completa e confirma oferta
    Dado um período letivo e uma disciplina disponíveis
    Quando a coordenação vincula professor, sala e horário à turma e confirma a oferta
    Então a turma é ofertada com status ofertada

  Cenário: Coordenação vincula sala com capacidade insuficiente à turma
    Dado um período letivo e uma disciplina disponíveis
    Quando a coordenação vincula uma sala com capacidade insuficiente à turma
    Então o sistema rejeita a vinculação informando capacidade insuficiente
