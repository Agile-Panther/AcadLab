-- =============================================================================
-- V3__ajusta_caso_estudante_demo.sql
-- O estudante de referência (id=1) tinha um caso ATIVO no seed, o que bloqueava
-- o fluxo "Solicitar apoio" (RN: um único caso ativo por estudante).
-- Deixamos o caso como ENCERRADO: a tela mantém o histórico e a solicitação
-- passa a funcionar via reabertura (RN1 — reabertura de caso encerrado).
-- =============================================================================

UPDATE caso_psicopedagogico
   SET status = 'ENCERRADO'
 WHERE id = 5;

-- Atendimento do caso 5 passa a ser conclusão final (coerência do encerramento).
UPDATE atendimento_caso
   SET conclusao_final = true
 WHERE caso_id = 5;
