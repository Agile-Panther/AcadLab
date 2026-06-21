# language: pt

Funcionalidade: Consolidar resultados no histórico acadêmico

  Cenário: Resultado de turma encerrada é consolidado com sucesso
    Dado um histórico acadêmico de estudante ativo para consolidação
    Quando a secretaria consolida o resultado de uma turma encerrada com situação "APROVADO"
    Então o registro é adicionado ao histórico com a situação "APROVADO"

  Cenário: Resultado de turma não encerrada não pode ser consolidado
    Dado um histórico acadêmico de estudante ativo para consolidação
    Quando a secretaria tenta consolidar resultado de turma não encerrada
    Então o sistema deve rejeitar informando "a turma ainda não foi encerrada"

  Cenário: Consolidação sem situação acadêmica é rejeitada
    Dado um histórico acadêmico de estudante ativo para consolidação
    Quando a secretaria tenta consolidar resultado sem informar situação acadêmica
    Então o sistema deve rejeitar informando "situação acadêmica não informada"
