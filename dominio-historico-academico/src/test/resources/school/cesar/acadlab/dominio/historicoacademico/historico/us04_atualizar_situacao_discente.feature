#language: pt
Funcionalidade: Atualizar situação acadêmica do estudante

  Cenário: Secretaria atualiza situação acadêmica com registro de auditoria
    Dado um histórico de estudante para atualização de situação discente
    Quando a secretaria atualiza a situação do estudante para "FORMANDO"
    Então a situação do estudante é "FORMANDO"
    E a trilha de auditoria registra a mudança com responsável e justificativa

  Cenário: Atualização da situação sem justificativa é rejeitada
    Dado um histórico de estudante para atualização de situação discente
    Quando a secretaria tenta atualizar a situação sem informar justificativa
    Então o sistema rejeita a atualização de situação
