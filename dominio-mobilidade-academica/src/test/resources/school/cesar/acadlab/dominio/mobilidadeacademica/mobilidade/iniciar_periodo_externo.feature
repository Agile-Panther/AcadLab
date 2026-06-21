# language: pt

Funcionalidade: Iniciar período externo de mobilidade

  Cenário: Mobilidade autorizada inicia período externo com sucesso
    Dado uma mobilidade autorizada para iniciar período do estudante id 11
    Quando o período externo é iniciado em "2025-03-01"
    Então a mobilidade tem status EM_ANDAMENTO

  Cenário: Iniciar período externo em mobilidade não autorizada é rejeitado
    Dado uma mobilidade apenas solicitada do estudante id 12
    Quando o período externo é iniciado em "2025-03-01"
    Então o sistema deve rejeitar informando "mobilidade deve estar autorizada para iniciar"
