-- ─── CATEGORIAS DE HORAS COMPLEMENTARES ──────────────────────────────────────
INSERT INTO categoria_atividade (id, nome, limite_horas) VALUES
  (1, 'Competições & Hackathons', 60),
  (2, 'Monitoria',                60),
  (3, 'Cursos & Certificações',   80),
  (4, 'Produção científica',      40)
ON CONFLICT (id) DO NOTHING;
