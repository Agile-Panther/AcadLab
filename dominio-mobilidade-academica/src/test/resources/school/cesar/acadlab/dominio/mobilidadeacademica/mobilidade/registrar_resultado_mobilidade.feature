# language: pt

Funcionalidade: Registrar resultado de mobilidade

  Cenário: Secretaria registra resultado com comprovante anexado
    Dado uma mobilidade com item autorizado e comprovante anexado para estudante id 6
    Quando a secretaria com id 1 registra o resultado da disciplina externa 10
    Então o resultado da disciplina externa 10 é registrado com sucesso

  Cenário: Registro de resultado sem comprovante é rejeitado
    Dado uma mobilidade com item autorizado sem comprovante para estudante id 7
    Quando a secretaria com id 1 tenta registrar resultado da disciplina 10 sem comprovante
    Então o sistema deve rejeitar informando "comprovante de resultado é obrigatório"

  Cenário: Mobilidade muda para CONCLUIDA quando todos os resultados são registrados
    Dado uma mobilidade com item autorizado e comprovante anexado para estudante id 13
    Quando a secretaria com id 1 registra o resultado da disciplina externa 10
    Então a mobilidade deve estar com status CONCLUIDA
