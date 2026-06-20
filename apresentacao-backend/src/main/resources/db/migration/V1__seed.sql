-- =============================================================================
-- V1__seed.sql — Dados de demonstração do AcadLab
-- Estudante de referência: id=1 (Maria Santos) | Curso: id=1 (Eng. Software)
-- =============================================================================

-- ─── SALAS ───────────────────────────────────────────────────────────────────
INSERT INTO sala (id, nome, capacidade, ativa) VALUES
  (1, 'Sala 101',  40, true),
  (2, 'Lab 201',   30, true),
  (3, 'Sala 301',  45, true),
  (4, 'Lab EAD',   60, true)
ON CONFLICT (id) DO NOTHING;

-- ─── PROFESSORES ─────────────────────────────────────────────────────────────
INSERT INTO professor (id, nome, ativo) VALUES
  (1,  'Carlos Lima',        true),
  (2,  'Ana Souza',          true),
  (3,  'Marcos Rodrigues',   true),
  (4,  'Dra. Lúcia Mendes',  true),
  (5,  'Pedro Alves',        true),
  (10, 'Dra. Lúcia Mendes',  true)
ON CONFLICT (id) DO NOTHING;

-- ─── MATRIZ CURRICULAR ───────────────────────────────────────────────────────
INSERT INTO matriz_curricular (id, curso_id, nome, carga_horaria_minima, creditos_exigidos, maximo_trancamentos, status) VALUES
  (1, 1, 'Engenharia de Software 2024.1', 3600, 240, 4, 'ATIVA')
ON CONFLICT (id) DO NOTHING;

-- ─── PERÍODO LETIVO ──────────────────────────────────────────────────────────
INSERT INTO periodo_letivo (id, curso_id, ano, semestre, data_inicio, data_fim, status) VALUES
  (1, 1, 2025, 2, '2025-07-14', '2025-11-30', 'EM_ANDAMENTO'),
  (2, 1, 2025, 1, '2025-02-10', '2025-06-30', 'ENCERRADO')
ON CONFLICT (id) DO NOTHING;

-- janelas do período em andamento
INSERT INTO janela_academica (periodo_letivo_id, tipo, data_inicio, data_fim) VALUES
  (1, 'MATRICULA',       '2025-07-14', '2025-07-25'),
  (1, 'AJUSTE',          '2025-07-28', '2025-08-01'),
  (1, 'TRANCAMENTO',     '2025-07-14', '2025-09-15'),
  (1, 'LANCAMENTO_NOTAS','2025-10-01', '2025-11-30'),
  (1, 'REVISAO_NOTAS',   '2025-11-01', '2025-11-30');

-- ─── TURMAS ──────────────────────────────────────────────────────────────────
INSERT INTO turma (id, periodo_letivo_id, disciplina_id, professor_id, sala_id, modalidade, capacidade, status) VALUES
  (1, 1, 101, 1, 1, 'PRESENCIAL', 40, 'OFERTADA'),
  (2, 1, 201, 2, 2, 'PRESENCIAL', 30, 'OFERTADA'),
  (3, 1, 301, 3, 3, 'HIBRIDO',   45, 'OFERTADA'),
  (4, 1, 401, 1, 4, 'EAD',       60, 'OFERTADA'),
  (5, 1, 501, 2, 1, 'PRESENCIAL', 35, 'OFERTADA'),
  (6, 1, 601, 4, 2, 'PRESENCIAL', 30, 'OFERTADA'),
  (7, 1, 701, 3, 4, 'EAD',       50, 'OFERTADA'),
  (8, 1, 801, 1, 1, 'HIBRIDO',   40, 'OFERTADA')
ON CONFLICT (id) DO NOTHING;

-- horários semanais de cada turma
INSERT INTO horario_aula (turma_id, dia_semana, hora_inicio, hora_fim) VALUES
  (1, 'MONDAY',    '08:00:00', '10:00:00'),
  (1, 'WEDNESDAY', '08:00:00', '10:00:00'),
  (2, 'MONDAY',    '14:00:00', '16:00:00'),
  (2, 'FRIDAY',    '16:00:00', '18:00:00'),
  (3, 'TUESDAY',   '10:00:00', '12:00:00'),
  (3, 'THURSDAY',  '10:00:00', '12:00:00'),
  (4, 'WEDNESDAY', '19:00:00', '21:00:00'),
  (4, 'FRIDAY',    '19:00:00', '21:00:00'),
  (5, 'FRIDAY',    '08:00:00', '12:00:00'),
  (6, 'TUESDAY',   '14:00:00', '16:00:00'),
  (6, 'THURSDAY',  '14:00:00', '16:00:00'),
  (7, 'WEDNESDAY', '14:00:00', '18:00:00'),
  (8, 'MONDAY',    '10:00:00', '12:00:00'),
  (8, 'WEDNESDAY', '10:00:00', '12:00:00')
ON CONFLICT DO NOTHING;

-- ─── MATRÍCULA ───────────────────────────────────────────────────────────────
INSERT INTO matricula (id, estudante_id, periodo_letivo_id, limite_creditos, status) VALUES
  (1, 1, 1, 24, 'CONFIRMADA'),
  (2, 2, 1, 24, 'CONFIRMADA'),
  (3, 3, 1, 20, 'CONFIRMADA')
ON CONFLICT (id) DO NOTHING;

-- itens de matrícula — usa IDENTITY: id é gerado automaticamente
INSERT INTO item_matricula (matricula_id, turma_id, disciplina_id, creditos, status_item) VALUES
  (1, 1, 101, 4, 'CONFIRMADO'),
  (1, 2, 201, 4, 'CONFIRMADO'),
  (1, 3, 301, 4, 'CONFIRMADO'),
  (2, 1, 101, 4, 'CONFIRMADO'),
  (2, 4, 401, 4, 'CONFIRMADO'),
  (3, 2, 201, 4, 'CONFIRMADO'),
  (3, 3, 301, 4, 'CONFIRMADO');

-- ─── DIÁRIO DE TURMA ─────────────────────────────────────────────────────────
INSERT INTO diario_turma (id, turma_id, periodo_letivo_id, professor_responsavel_id,
                          data_inicio_periodo, data_fim_periodo,
                          media_minima, frequencia_minima, status) VALUES
  (1, 1, 1, 1, '2025-07-14', '2025-11-30', 7.0, 75.0, 'ABERTO'),
  (2, 2, 1, 2, '2025-07-14', '2025-11-30', 7.0, 75.0, 'ABERTO'),
  (3, 3, 1, 3, '2025-07-14', '2025-11-30', 6.0, 75.0, 'ABERTO'),
  (4, 4, 1, 1, '2025-07-14', '2025-11-30', 7.0, 75.0, 'ABERTO')
ON CONFLICT (id) DO NOTHING;

-- estudantes ativos em cada diário
INSERT INTO estudante_ativo_diario (diario_id, estudante_id) VALUES
  (1, 1), (1, 2), (1, 3),
  (2, 1), (2, 2),
  (3, 1), (3, 3),
  (4, 2);

-- ─── HISTÓRICO ACADÊMICO ─────────────────────────────────────────────────────
INSERT INTO historico_academico (id, estudante_id, matriz_curricular_id, situacao_discente) VALUES
  (1, 1, 1, 'ATIVO'),
  (2, 2, 1, 'ATIVO'),
  (3, 3, 1, 'ATIVO')
ON CONFLICT (id) DO NOTHING;

-- registros de disciplinas de períodos anteriores (estudante 1)
INSERT INTO registro_disciplina (historico_id, registro_id, disciplina_id, turma_id,
                                  periodo_letivo_id, nota, frequencia, situacao_academica) VALUES
  (1, 1,  50, 10, 2, 8.5, 92.0, 'APROVADO'),
  (1, 2,  51, 11, 2, 7.2, 88.0, 'APROVADO'),
  (1, 3,  52, 12, 2, 9.0, 95.0, 'APROVADO'),
  (1, 4,  53, 13, 2, 5.0, 70.0, 'REPROVADO_NOTA'),
  (1, 5,  54, 14, 2, 8.8, 80.0, 'APROVADO'),
  (2, 6,  50, 10, 2, 7.0, 78.0, 'APROVADO'),
  (2, 7,  51, 11, 2, 6.5, 60.0, 'REPROVADO_FALTA');

-- acompanhamentos acadêmicos
INSERT INTO acompanhamento_academico (historico_id, acompanhamento_id, observacao, data) VALUES
  (1, 1, 'Estudante apresenta desempenho acima da média. Candidata a bolsa de iniciação científica.', '2025-03-15'),
  (1, 2, 'Reprovação em disciplina de fundamentos no período anterior. Acompanhamento recomendado.', '2025-07-01'),
  (2, 3, 'Estudante com dificuldade em presença. Orientado sobre política de frequência.', '2025-03-20');

-- ─── SOLICITAÇÕES ACADÊMICAS ─────────────────────────────────────────────────
INSERT INTO solicitacao_academica (id, estudante_id, periodo_letivo_id, tipo, protocolo_id,
                                    descricao, data_abertura, status,
                                    possui_impacto_academico, alteracoes_vinculadas) VALUES
  (1, 1, 1, 'REVISAO_NOTA',           1001,
   'Solicito revisão da nota da Prova 1 de BD302 — discordância no critério da questão 3.',
   '2025-09-10', 'PENDENTE_ANALISE', false, false),
  (2, 2, 1, 'TRANCAMENTO_DISCIPLINA', 1002,
   'Solicito trancamento da disciplina ES303 por afastamento médico (laudo em anexo).',
   '2025-09-12', 'PENDENTE_ANALISE', true,  false),
  (3, 3, 1, 'APROVEITAMENTO_EXTERNO', 1003,
   'Solicito aproveitamento da disciplina Algoritmos cursada na UFPE (histórico em anexo).',
   '2025-09-05', 'EM_ANALISE',       false, false),
  (4, 1, 1, 'REVISAO_NOTA',           1004,
   'Revisão da nota de Cálculo Diferencial — Prova 2.',
   '2025-09-08', 'DEFERIDA',         false, false)
ON CONFLICT (id) DO NOTHING;

-- ─── INTEGRALIZAÇÃO CURRICULAR ───────────────────────────────────────────────
INSERT INTO integralizacao_curricular (id, estudante_id, matriz_curricular_id, status) VALUES
  (1, 1, 1, 'EM_ANALISE'),
  (2, 4, 1, 'APTO')
ON CONFLICT (id) DO NOTHING;

INSERT INTO item_checklist_integralizacao (integralizacao_id, tipo, descricao, cumprido) VALUES
  (1, 'CREDITOS',                'Mínimo de 240 créditos cursados e aprovados',         false),
  (1, 'CARGA_HORARIA',           'Carga horária mínima de 3600 horas',                   false),
  (1, 'ATIVIDADES_COMPLEMENTARES','Mínimo de 200 h de atividades complementares aprovadas', true),
  (1, 'ESTAGIO',                 'Estágio obrigatório concluído e relatório aprovado',   false),
  (2, 'CREDITOS',                'Mínimo de 240 créditos cursados e aprovados',          true),
  (2, 'CARGA_HORARIA',           'Carga horária mínima de 3600 horas',                   true),
  (2, 'ATIVIDADES_COMPLEMENTARES','Mínimo de 200 h de atividades complementares aprovadas', true),
  (2, 'ESTAGIO',                 'Estágio obrigatório concluído e relatório aprovado',   true);

-- ─── ATIVIDADES COMPLEMENTARES ───────────────────────────────────────────────
INSERT INTO atividade_complementar (id, estudante_id, categoria_id, descricao,
                                     horas_submetidas, horas_aprovadas, data_realizacao, status) VALUES
  (1, 1, 1, 'Hackathon CESAR 2025 — 1.º lugar na trilha de IA',                    40, 40, '2025-04-12', 'DEFERIDA'),
  (2, 1, 2, 'Monitoria em Algoritmos e Estruturas de Dados — semestre 2025.1',     60, 60, '2025-06-30', 'DEFERIDA'),
  (3, 1, 3, 'Curso Machine Learning Specialization — Coursera (certificado)',       30,  0, '2025-05-20', 'PENDENTE'),
  (4, 1, 4, 'Publicação em anais do SBES 2025 — coautora',                         20, 20, '2025-03-10', 'DEFERIDA'),
  (5, 2, 1, 'Maratona de Programação ICPC 2025 — 3.º lugar regional',              20, 20, '2025-04-05', 'DEFERIDA'),
  (6, 2, 3, 'Curso Docker e Kubernetes — Udemy',                                   20,  0, '2025-06-01', 'INDEFERIDA')
ON CONFLICT (id) DO NOTHING;

-- ─── PERMANÊNCIA ACADÊMICA — EDITAIS ─────────────────────────────────────────
INSERT INTO edital_permanencia (id, programa, vagas,
                                 prazo_inscricao_inicio, prazo_inscricao_fim,
                                 prazo_recurso_inicio,   prazo_recurso_fim,
                                 prazo_renovacao, status) VALUES
  (1, 'Bolsa Permanência 2025.2',   50, '2025-07-14', '2025-07-31', '2025-08-10', '2025-08-17', '2025-12-01', 'INSCRICOES_ABERTAS'),
  (2, 'Auxílio Moradia 2025.2',     30, '2025-07-14', '2025-07-31', '2025-08-10', '2025-08-17', null,         'INSCRICOES_ABERTAS'),
  (3, 'Auxílio Alimentação 2025.1', 100,'2025-01-20', '2025-02-05', '2025-02-15', '2025-02-22', null,         'ENCERRADO')
ON CONFLICT (id) DO NOTHING;

-- ─── APOIO PSICOPEDAGÓGICO — CASOS ───────────────────────────────────────────
INSERT INTO caso_psicopedagogico (id, estudante_id, responsavel_id, status) VALUES
  (1, 5,  10, 'EM_ATENDIMENTO'),
  (2, 7,  10, 'ABERTO'),
  (3, 3,  10, 'ABERTO'),
  (4, 9,  10, 'ENCERRADO')
ON CONFLICT (id) DO NOTHING;

-- ─── MOBILIDADE ACADÊMICA ────────────────────────────────────────────────────
INSERT INTO mobilidade_academica (id, estudante_id, instituicao_destino, status) VALUES
  (1, 1, 'Universidade do Porto — Portugal', 'SOLICITADA'),
  (2, 4, 'UFPE — Brasil',                    'CONCLUIDA'),
  (3, 6, 'MIT — Estados Unidos',             'AUTORIZADA')
ON CONFLICT (id) DO NOTHING;

-- ─── COBRANÇAS ───────────────────────────────────────────────────────────────
INSERT INTO cobranca (id, contrato_id, estudante_id, periodo_letivo_id,
                       valor_base, valor_atual, vencimento, versao, status) VALUES
  (1, 1, 1, 1, 2500.00, 2500.00, '2025-08-10', 1, 'ABERTA'),
  (2, 1, 1, 1, 2500.00, 2500.00, '2025-09-10', 1, 'ABERTA'),
  (3, 1, 1, 1, 2500.00, 2500.00, '2025-07-10', 1, 'PAGA'),
  (4, 2, 2, 1, 2500.00, 2125.00, '2025-08-10', 2, 'ABERTA'),
  (5, 2, 2, 1, 2500.00, 2125.00, '2025-09-10', 2, 'ABERTA'),
  (6, 3, 3, 1, 2500.00, 2500.00, '2025-08-10', 1, 'CONTESTADA')
ON CONFLICT (id) DO NOTHING;

-- ─── OPORTUNIDADES DE ESTÁGIO ────────────────────────────────────────────────
INSERT INTO oportunidade_estagio (id, empresa_id, descricao, carga_horaria_total, status) VALUES
  (1, 1, 'Estágio em Desenvolvimento Front-end React/TypeScript — Acme Tech',    480, 'PUBLICADA'),
  (2, 2, 'Estágio em Ciência de Dados e Machine Learning — DataBank',            480, 'PUBLICADA'),
  (3, 3, 'Estágio em QA e Automação de Testes (Selenium/Cypress) — Studio Nova', 480, 'PUBLICADA'),
  (4, 4, 'Estágio em Back-end Java Spring Boot — Fintech Lúmen',                 480, 'CADASTRADA'),
  (5, 5, 'Estágio em DevOps e Cloud AWS — CloudSystems',                         480, 'ENCERRADA')
ON CONFLICT (id) DO NOTHING;
