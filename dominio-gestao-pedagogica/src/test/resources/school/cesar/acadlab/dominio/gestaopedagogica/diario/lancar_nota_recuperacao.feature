# language: pt

Funcionalidade: Registrar nota de recuperação

  Cenário: Professor registra nota de recuperação aprovada dentro do período
    Dado um diário com estudante em situação de recuperação
    Quando o professor registra nota de recuperação aprovada dentro do período
    Então o resultado do estudante é atualizado para aprovado após recuperação

  Cenário: Nota de recuperação para estudante aprovado é rejeitada
    Dado um diário com estudante aprovado sem necessidade de recuperação
    Quando o professor tenta registrar nota de recuperação para estudante aprovado
    Então o sistema deve rejeitar informando "estudante não está em situação de recuperação"

  Cenário: Nota de recuperação após fim do período é rejeitada
    Dado um diário com estudante que necessita nota de recuperação
    Quando o professor tenta registrar nota de recuperação após o fim do período
    Então o sistema deve rejeitar informando "período letivo já encerrado"
