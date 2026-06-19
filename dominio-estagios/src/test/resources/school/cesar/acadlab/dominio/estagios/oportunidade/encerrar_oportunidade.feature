# language: pt

Funcionalidade: Encerrar oportunidade de estágio

  Cenário: Setor de estágios encerra oportunidade publicada com motivo
    Dado uma oportunidade publicada para encerramento
    Quando o setor de estágios encerra a oportunidade por "VAGAS_PREENCHIDAS"
    Então a oportunidade fica com status ENCERRADA

  Cenário: Tentativa de encerrar oportunidade ainda não publicada
    Dado uma oportunidade ainda não publicada para encerramento
    Quando o setor de estágios tenta encerrar a oportunidade
    Então o sistema deve rejeitar informando "somente oportunidades publicadas podem ser encerradas"
