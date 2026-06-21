-- =============================================================================
-- V2__seed_itens_matriz_curricular.sql — Disciplinas e pré-requisitos da
-- matriz curricular semeada em V1 (id=1, curso 1, ATIVA).
-- IDs de disciplina alinhados aos já usados em TURMA (101, 201, 301, 401).
-- =============================================================================

INSERT INTO item_matriz (matriz_id, disciplina_id, tipo, carga_horaria, creditos) VALUES
  (1, 101, 'OBRIGATORIA', 80, 4),
  (1, 201, 'OBRIGATORIA', 80, 4),
  (1, 301, 'OBRIGATORIA', 80, 4),
  (1, 401, 'OPTATIVA',    60, 3);

INSERT INTO pre_requisito (matriz_id, disciplina_id, pre_requisito_id) VALUES
  (1, 201, 101),
  (1, 301, 201);
