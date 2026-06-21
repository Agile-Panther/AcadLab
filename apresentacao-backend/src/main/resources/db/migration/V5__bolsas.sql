-- ─── BOLSAS (F-13 sub-A) ─────────────────────────────────────────────────────
INSERT INTO bolsa (id, estudante_id, tipo, percentual, validade, status) VALUES
  (1, 1, 'MERITO',   50.00, '2025-12-31', 'ATIVA'),
  (2, 2, 'PROUNI',  100.00, '2026-12-31', 'ATIVA'),
  (3, 3, 'FIES',     75.00, '2025-06-30', 'EM_RENOVACAO'),
  (4, 4, 'CONVENIO', 20.00, '2024-12-31', 'SUSPENSA')
ON CONFLICT (id) DO NOTHING;
