#language: pt
Funcionalidade: Solicitar Revisão de Atividade Complementar

  Cenário: Solicitar revisão de atividade indeferida não contabilizada
    Dado uma atividade complementar no estado indeferida
    E a atividade não foi contabilizada na integralização curricular
    Quando o estudante solicita revisão com justificativa "Enviei documentação complementar"
    Então a atividade deve ter status REVISAO_SOLICITADA

  Cenário: Rejeitar revisão de atividade já contabilizada
    Dado uma atividade complementar no estado indeferida
    E a atividade já foi contabilizada na integralização curricular
    Quando o estudante tenta solicitar revisão da atividade
    Então deve ser lançada uma exceção de atividade já contabilizada
