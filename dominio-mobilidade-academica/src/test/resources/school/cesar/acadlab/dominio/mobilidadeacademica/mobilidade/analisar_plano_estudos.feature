# language: pt

Funcionalidade: Analisar plano de estudos

  Cenário: Coordenador aprova item do plano com carga horária suficiente
    Dado uma mobilidade autorizada para análise de plano com estudante id 3
    Quando o coordenador adiciona item ao plano com disciplina externa 10 equivalente 20 carga externa 60 carga equivalente 60
    Então o item do plano tem status AUTORIZADO

  Cenário: Aprovação de item do plano com carga horária externa insuficiente é rejeitada
    Dado uma mobilidade autorizada para análise de plano com estudante id 4
    Quando o coordenador tenta adicionar item ao plano com carga externa 30 menor que equivalente 60
    Então o sistema deve rejeitar informando "carga horária externa é inferior à exigida para equivalência"

  Cenário: Registro de resultado para disciplina fora do plano autorizado é rejeitado
    Dado uma mobilidade autorizada sem itens no plano para estudante id 5
    Quando a secretaria tenta registrar resultado para disciplina 99 fora do plano
    Então o sistema deve rejeitar informando "disciplina não consta no plano de estudos autorizado"
