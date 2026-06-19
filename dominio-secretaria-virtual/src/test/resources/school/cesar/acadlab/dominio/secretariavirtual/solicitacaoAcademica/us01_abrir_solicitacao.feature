#language: pt
Funcionalidade: Abrir e submeter solicitação acadêmica

  Cenário: Estudante abre solicitação dentro do prazo com documentos obrigatórios
    Dado um estudante sem solicitação aberta do tipo "APROVEITAMENTO_DISCIPLINA"
    E os documentos obrigatórios estão anexados
    E o prazo do calendário acadêmico está vigente
    Quando o estudante abre a solicitação acadêmica
    Então o sistema registra a solicitação com sucesso
    E a solicitação é criada com status "PENDENTE_ANALISE"
    E um protocolo é gerado para a solicitação

  Cenário: Estudante tenta abrir solicitação fora do prazo do calendário
    Dado um estudante sem solicitação aberta do tipo "TRANCAMENTO_DISCIPLINA"
    E o prazo do calendário acadêmico está encerrado
    Quando o estudante abre a solicitação acadêmica
    Então o sistema rejeita a abertura por prazo expirado

  Cenário: Estudante tenta abrir solicitação duplicada do mesmo tipo no período
    Dado um estudante com solicitação aberta do tipo "TRANCAMENTO_DISCIPLINA"
    E o prazo do calendário acadêmico está vigente
    Quando o estudante abre a solicitação acadêmica
    Então o sistema rejeita a abertura por duplicidade

  Cenário: Estudante abre múltiplas solicitações de revisão de nota no período
    Dado um estudante com solicitação aberta do tipo "REVISAO_DE_NOTA"
    E o prazo do calendário acadêmico está vigente
    Quando o estudante abre outra solicitação de revisão de nota
    Então o sistema registra a solicitação com sucesso

  Cenário: Estudante tenta abrir solicitação sem documentos obrigatórios
    Dado um estudante sem solicitação aberta do tipo "APROVEITAMENTO_DISCIPLINA"
    E os documentos obrigatórios não estão anexados
    E o prazo do calendário acadêmico está vigente
    Quando o estudante abre a solicitação acadêmica
    Então o sistema rejeita a abertura por documentação incompleta
