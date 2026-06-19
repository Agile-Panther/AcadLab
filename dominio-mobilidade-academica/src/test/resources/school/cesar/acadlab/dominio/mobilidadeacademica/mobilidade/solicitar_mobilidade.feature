# language: pt

Funcionalidade: Solicitar mobilidade acadêmica

  Cenário: Estudante solicita mobilidade e coordenador autoriza
    Dado um estudante com id 1 deseja mobilidade para "MIT"
    Quando o estudante solicita a mobilidade acadêmica
    Então a mobilidade é registrada com status SOLICITADA
    Quando o coordenador com id 1 autoriza a mobilidade
    Então a mobilidade tem status AUTORIZADA

  Cenário: Autorizar uma mobilidade já autorizada é rejeitado
    Dado uma mobilidade acadêmica já autorizada para o estudante com id 2
    Quando o coordenador com id 1 tenta autorizar a mobilidade já autorizada
    Então o sistema deve rejeitar informando "mobilidade já se encontra autorizada"
