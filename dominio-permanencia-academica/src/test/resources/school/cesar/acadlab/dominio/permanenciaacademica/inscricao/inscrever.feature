#language: pt
Funcionalidade: Inscrever-se em programa de permanência

  Cenário: Estudante se inscreve dentro do prazo e atendendo aos critérios
    Dado existe um edital com inscrições abertas e o prazo atual é válido
    E o estudante atende aos critérios de elegibilidade
    Quando o estudante solicita inscrição no edital
    Então o sistema registra a inscrição com status pendente

  Cenário: Inscrição falha quando fora do prazo do edital
    Dado existe um edital cujo prazo de inscrição já encerrou
    Quando o estudante tenta se inscrever no edital
    Então o sistema informa que a inscrição está fora do prazo

  Cenário: Inscrição falha quando estudante não atende os critérios de elegibilidade
    Dado existe um edital com inscrições abertas e o prazo atual é válido
    E o estudante não atende aos critérios de elegibilidade
    Quando o estudante solicita inscrição no edital
    Então o sistema informa que o estudante não atende aos critérios de elegibilidade
