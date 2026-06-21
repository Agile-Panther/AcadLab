# language: pt

Funcionalidade: Agendamento de horário e contestação no caso psicopedagógico

  Cenário: Psicopedagogo marca horário com o aluno
    Dado um caso psicopedagógico em acompanhamento
    Quando o psicopedagogo marca um horário de atendimento
    Então o sistema registra o horário como agendado

  Cenário: Aluno contesta o horário e solicita troca
    Dado um caso psicopedagógico com horário agendado
    Quando o aluno contesta o horário sugerindo outro
    Então o sistema registra o horário como contestado com a justificativa do aluno

  Cenário: Reagendar após contestação volta o horário para agendado
    Dado um caso psicopedagógico com horário contestado pelo aluno
    Quando o psicopedagogo reagenda um novo horário
    Então o sistema registra o horário como agendado

  Cenário: Contestar sem horário agendado falha
    Dado um caso psicopedagógico em acompanhamento
    Quando o aluno tenta contestar um horário inexistente
    Então o sistema deve rejeitar informando "não há horário agendado para contestar"

  Cenário: Marcar horário no passado falha
    Dado um caso psicopedagógico em acompanhamento
    Quando o psicopedagogo tenta marcar um horário no passado
    Então o sistema deve rejeitar informando "deve ser no futuro"
    E nenhum horário é registrado no caso
