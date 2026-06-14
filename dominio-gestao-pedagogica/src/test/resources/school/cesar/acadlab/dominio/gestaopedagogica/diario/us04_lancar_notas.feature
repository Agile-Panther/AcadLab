#language: pt
Funcionalidade: Lançar notas e fechar resultado final

  Cenário: Professor fecha resultado final com nota aprovada
    Dado um diário com estudante matriculado com frequência suficiente e nota aprovada
    Quando o professor fecha o resultado do estudante
    Então o resultado do estudante é marcado como aprovado

  Cenário: Professor fecha resultado final com nota reprovada
    Dado um diário com estudante matriculado com frequência suficiente e nota reprovada
    Quando o professor fecha o resultado reprovado do estudante
    Então o resultado do estudante é marcado como reprovado por nota

  Cenário: Alteração de nota após fechamento é rejeitada
    Dado um diário com estudante cujo resultado já está fechado
    Quando o professor tenta alterar a nota após o fechamento
    Então o sistema rejeita a alteração informando RN-8

  Cenário: Estudante solicita revisão de nota dentro da janela
    Dado um diário com estudante matriculado para revisão de nota
    Quando o estudante solicita revisão de nota dentro da janela permitida
    Então a solicitação de revisão é registrada para o estudante

  Cenário: Solicitação de revisão fora da janela é rejeitada
    Dado um diário com estudante matriculado para revisão de nota
    Quando o estudante solicita revisão de nota após o fim da janela
    Então o sistema rejeita a revisão informando RN-9
