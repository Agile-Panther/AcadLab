#language: pt
Funcionalidade: Definir janelas acadêmicas do período letivo

  Cenário: Secretaria define janela de matrícula com datas válidas
    Dado um período letivo cadastrado sem janelas definidas
    Quando a secretaria define a janela de matrícula com datas válidas
    Então a janela de matrícula é registrada no período letivo

  Cenário: Secretaria redefine janela de matrícula substituindo a anterior
    Dado um período letivo cadastrado sem janelas definidas
    Quando a secretaria redefine a janela de matrícula com novas datas
    Então apenas uma janela de matrícula existe no período letivo
