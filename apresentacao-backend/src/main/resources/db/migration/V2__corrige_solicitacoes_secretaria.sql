-- =============================================================================
-- V2__corrige_solicitacoes_secretaria.sql
-- Corrige valores de `tipo` semeados em V1 que não existem no enum TipoSolicitacao
-- (REVISAO_NOTA / APROVEITAMENTO_EXTERNO) e que quebravam o mapeamento do Hibernate
-- ao ler a tabela (causando HTTP 500 nas telas da Secretaria Virtual).
-- Também adiciona solicitações extras do estudante 1 para enriquecer as visões.
-- =============================================================================

-- ─── Correção de valores legados de tipo (enum) ──────────────────────────────
UPDATE solicitacao_academica SET tipo = 'REVISAO_DE_NOTA'           WHERE tipo = 'REVISAO_NOTA';
UPDATE solicitacao_academica SET tipo = 'APROVEITAMENTO_DISCIPLINA' WHERE tipo = 'APROVEITAMENTO_EXTERNO';

-- ─── Solicitações adicionais do estudante 1 (Maria Santos) ───────────────────
INSERT INTO solicitacao_academica (id, estudante_id, periodo_letivo_id, tipo, protocolo_id,
                                    descricao, data_abertura, status,
                                    possui_impacto_academico, alteracoes_vinculadas) VALUES
  (5, 1, 1, 'SEGUNDA_VIA_DOCUMENTO', 1005,
   'Solicito segunda via da declaração de matrícula para fins de estágio.',
   '2025-09-14', 'PENDENTE_COMPLEMENTACAO', false, false),
  (6, 1, 2, 'DECLARACAO_VINCULO',    1006,
   'Solicito declaração de vínculo institucional para abertura de conta bancária.',
   '2025-08-20', 'CONCLUIDA',        false, true)
ON CONFLICT (id) DO NOTHING;
