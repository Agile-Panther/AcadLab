#language: pt
Funcionalidade: Submeter Atividade Complementar

  Cenário: Submeter atividade com vínculo ativo e certificado único
    Dado um estudante com vínculo ativo no período de realização da atividade
    E o certificado "CERT-001" ainda não foi utilizado
    Quando o estudante submete a atividade da categoria 1 com 40 horas realizada em "15/03/2025" com certificado "CERT-001"
    Então a atividade deve ser salva com status PENDENTE

  Cenário: Rejeitar atividade realizada fora do período de vínculo
    Dado um estudante sem vínculo ativo na data de realização
    Quando o estudante tenta submeter a atividade da categoria 1 com 40 horas realizada em "15/03/2025" com certificado "CERT-002"
    Então deve ser lançada uma exceção de vínculo inativo

  Cenário: Rejeitar atividade com certificado já utilizado
    Dado um estudante com vínculo ativo no período de realização da atividade
    E o certificado "CERT-003" já foi utilizado anteriormente
    Quando o estudante tenta submeter a atividade da categoria 1 com 40 horas realizada em "15/03/2025" com certificado "CERT-003"
    Então deve ser lançada uma exceção de certificado duplicado
