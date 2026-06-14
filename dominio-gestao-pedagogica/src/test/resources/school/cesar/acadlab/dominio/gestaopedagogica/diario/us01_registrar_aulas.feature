#language: pt
Funcionalidade: Registrar aulas no diário de turma

  Cenário: Professor registra aula dentro do período letivo
    Dado um diário de turma com professor responsável cadastrado
    Quando o professor responsável registra uma aula dentro do período
    Então o registro de aula é adicionado ao diário

  Cenário: Professor não responsável não pode registrar aula
    Dado um diário de turma com professor responsável cadastrado
    Quando outro professor tenta registrar uma aula
    Então o sistema rejeita o registro informando RN-1

  Cenário: Professor corrige o conteúdo de uma aula com diário aberto
    Dado um diário de turma com um registro de aula existente
    Quando o professor responsável corrige o conteúdo da aula
    Então o registro de aula é atualizado com o novo conteúdo

  Cenário: Correção de aula com diário fechado é rejeitada
    Dado um registro de aula com diário explicitamente fechado
    Quando o professor tenta corrigir o conteúdo com diário fechado
    Então o sistema rejeita a correção informando RN-10
