#language: pt
Funcionalidade: Registrar ação de permanência acadêmica

  Cenário: Ação de permanência registrada com sucesso
    Dado um coordenador autorizado
    Quando o coordenador registra uma ação de permanência com indicadores agregados
    Então o sistema registra a ação de permanência com sucesso

  Cenário: Registrar ação sem coordenador falha
    Dado indicadores agregados de atendimento disponíveis
    Quando alguém tenta registrar uma ação de permanência sem identificação de coordenador
    Então o sistema informa que o coordenador é obrigatório para registrar ações de permanência
