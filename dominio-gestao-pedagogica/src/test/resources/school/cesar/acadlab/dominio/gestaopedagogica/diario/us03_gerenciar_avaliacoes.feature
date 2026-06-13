Funcionalidade: Criar avaliações com pesos e prazos

  Cenário: Professor cria avaliação com prazo dentro do período
    Dado um diário de turma vazio para gerenciamento de avaliações
    Quando o professor adiciona uma avaliação com prazo dentro do período
    Então a avaliação é adicionada ao diário

  Cenário: Avaliação com prazo fora do período é rejeitada
    Dado um diário de turma vazio para gerenciamento de avaliações
    Quando o professor adiciona uma avaliação com prazo fora do período
    Então o sistema rejeita a avaliação informando RN-6

  Cenário: Soma dos pesos das avaliações não pode ultrapassar 100 por cento
    Dado um diário de turma vazio para gerenciamento de avaliações
    Quando o professor adiciona avaliações cuja soma dos pesos ultrapassa 100 por cento
    Então o sistema rejeita a segunda avaliação informando RN-5
