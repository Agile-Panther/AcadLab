Feature: Registrar ação de permanência acadêmica

  Scenario: Ação de permanência registrada com sucesso
    Given um coordenador autorizado
    When o coordenador registra uma ação de permanência com indicadores agregados
    Then o sistema registra a ação de permanência com sucesso

  Scenario: Registrar ação sem coordenador falha
    Given indicadores agregados de atendimento disponíveis
    When alguém tenta registrar uma ação de permanência sem identificação de coordenador
    Then o sistema informa que o coordenador é obrigatório para registrar ações de permanência
