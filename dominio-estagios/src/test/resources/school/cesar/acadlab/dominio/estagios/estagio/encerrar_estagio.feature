#language: pt
Funcionalidade: Encerrar estágio

  Cenário: Estudante solicita encerramento e coordenador homologa
    Dado um estágio em andamento para o estudante de id 20
    Quando o estudante solicita o encerramento do estágio
    Então o estágio possui status ENCERRAMENTO_SOLICITADO
    Quando o coordenador de id 30 homologa o encerramento
    Então o estágio possui status ENCERRADO

  Cenário: Solicitação de encerramento rejeitada quando não está em andamento
    Dado um estágio com encerramento já solicitado
    Quando o estudante tenta solicitar encerramento novamente
    Então o sistema rejeita o encerramento com mensagem sobre RN-11

  Cenário: Homologação rejeitada sem solicitação prévia
    Dado um estágio em andamento para o estudante de id 20
    Quando o coordenador de id 30 tenta homologar sem solicitação
    Então o sistema rejeita a homologação com mensagem sobre RN-12
