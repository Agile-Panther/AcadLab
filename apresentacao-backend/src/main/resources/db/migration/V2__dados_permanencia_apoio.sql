-- =============================================================================
-- V2__dados_permanencia_apoio.sql — Dados de demonstração para F-10 e F-11
-- Estudante de referência (usuário atual do frontend): id=1
-- Psicopedagogo responsável: id=10 | Assistência Estudantil: id=1
-- Depende dos editais (1,2,3) e casos (1..4) criados em V1__seed.sql.
-- =============================================================================

-- ─── PERMANÊNCIA — INSCRIÇÕES ────────────────────────────────────────────────
INSERT INTO inscricao_permanencia (id, edital_id, estudante_id, status,
                                   recurso_interposto, pontuacao, data_inscricao) VALUES
  (1, 3, 1, 'INDEFERIDA', false,  0, '2025-01-28'),
  (2, 1, 1, 'DEFERIDA',   false, 88, '2025-07-15'),
  (3, 1, 2, 'PENDENTE',   false,  0, '2025-07-16'),
  (4, 1, 3, 'PENDENTE',   false,  0, '2025-07-17'),
  (5, 2, 5, 'PENDENTE',   false,  0, '2025-07-16'),
  (6, 2, 7, 'DEFERIDA',   false, 75, '2025-07-15')
ON CONFLICT (id) DO NOTHING;

-- ─── PERMANÊNCIA — BENEFÍCIOS ────────────────────────────────────────────────
INSERT INTO beneficio_permanencia (id, inscricao_id, estudante_id, edital_id, status,
                                   data_ativacao, prazo_renovacao, solicitou_renovacao) VALUES
  (1, 2, 1, 1, 'ATIVO', '2025-07-20', '2025-12-01', false),
  (2, 6, 7, 2, 'ATIVO', '2025-07-20', null,         false)
ON CONFLICT (id) DO NOTHING;

-- ─── APOIO — SOLICITAÇÕES (motivo/abertura dos casos) ────────────────────────
INSERT INTO solicitacao_apoio (id, estudante_id, descricao, data_solicitacao) VALUES
  (1, 5, 'Ansiedade relacionada a provas',        '2025-03-02'),
  (2, 7, 'Dificuldade de adaptação ao curso',     '2025-03-17'),
  (3, 3, 'Queda de rendimento acadêmico',         '2025-03-19'),
  (4, 9, 'Adaptação ao primeiro semestre',        '2024-08-10'),
  (5, 1, 'Ansiedade e organização dos estudos',   '2025-07-16')
ON CONFLICT (id) DO NOTHING;

-- ─── APOIO — NOVO CASO DO ESTUDANTE 1 (usuário atual) ────────────────────────
INSERT INTO caso_psicopedagogico (id, estudante_id, responsavel_id, status,
                                  triagem_prioridade, triagem_observacoes,
                                  triagem_responsavel_id, triagem_data) VALUES
  (5, 1, 10, 'EM_ATENDIMENTO', 'MEDIA',
   'Acompanhamento individual quinzenal.', 10, '2025-07-18')
ON CONFLICT (id) DO NOTHING;

-- ─── APOIO — TRIAGEM nos casos já existentes ─────────────────────────────────
-- (casos 2 e 3 permanecem ABERTO/sem triagem para demonstrar o fluxo de triagem)
UPDATE caso_psicopedagogico
   SET triagem_prioridade = 'MEDIA',
       triagem_observacoes = 'Prioridade média; acompanhamento individual.',
       triagem_responsavel_id = 10,
       triagem_data = '2025-03-05'
 WHERE id = 1 AND triagem_prioridade IS NULL;

UPDATE caso_psicopedagogico
   SET triagem_prioridade = 'BAIXA',
       triagem_observacoes = 'Caso de adaptação; baixa complexidade.',
       triagem_responsavel_id = 10,
       triagem_data = '2024-08-12'
 WHERE id = 4 AND triagem_prioridade IS NULL;

-- ─── APOIO — ATENDIMENTOS ────────────────────────────────────────────────────
INSERT INTO atendimento_caso (caso_id, observacoes, encaminhamento, conclusao_final, data) VALUES
  (1, 'Avaliação inicial. Técnicas de respiração apresentadas.', null, false, '2025-03-10'),
  (1, 'Evolução positiva no controle da ansiedade.',             null, false, '2025-03-18'),
  (4, 'Estudante adaptado; objetivos atingidos.',                null, true,  '2024-11-20'),
  (5, 'Primeiro atendimento. Plano de organização de estudos definido.', null, false, '2025-07-18');
