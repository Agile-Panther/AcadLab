Feature: Analisar plano de estudos

  Scenario: Coordenador aprova item do plano com carga horária suficiente
    Given uma mobilidade autorizada para análise de plano com estudante id 3
    When o coordenador adiciona item ao plano com disciplina externa 10 equivalente 20 carga externa 60 carga equivalente 60
    Then o item do plano tem status AUTORIZADO

  Scenario: Aprovação de item do plano com carga horária externa insuficiente é rejeitada
    Given uma mobilidade autorizada para análise de plano com estudante id 4
    When o coordenador tenta adicionar item ao plano com carga externa 30 menor que equivalente 60
    Then o sistema rejeita o item com mensagem sobre RN-3

  Scenario: Registro de resultado para disciplina fora do plano autorizado é rejeitado
    Given uma mobilidade autorizada sem itens no plano para estudante id 5
    When a secretaria tenta registrar resultado para disciplina 99 fora do plano
    Then o sistema rejeita o registro com mensagem sobre ausência no plano
