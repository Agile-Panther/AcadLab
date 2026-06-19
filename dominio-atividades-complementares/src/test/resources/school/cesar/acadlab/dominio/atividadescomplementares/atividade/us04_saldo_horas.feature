#language: pt
Funcionalidade: Visualizar Saldo de Horas Complementares

  Cenário: Saldo considera apenas atividades deferidas por categoria
    Dado o estudante 2 possui uma atividade DEFERIDA na categoria 1 com 40 horas aprovadas
    E o estudante 2 possui uma atividade PENDENTE na categoria 1 com 20 horas submetidas
    E o estudante 2 possui uma atividade INDEFERIDA na categoria 2 com 30 horas submetidas
    Quando consulto o saldo de horas do estudante 2
    Então o saldo da categoria 1 deve ser 40 horas
    E a categoria 2 não deve aparecer no saldo
