-- ─── CONTESTAÇÃO seed (cobrança 6, que já é CONTESTADA no V1) ─────────────────
-- V1 já aplicado não pode ser editado; populamos as colunas de contestação aqui.
UPDATE cobranca
   SET contestacao_requerente   = 3,
       contestacao_justificativa = 'Valor cobrado diverge do contrato firmado.',
       contestacao_data         = '2025-08-15',
       contestacao_status       = 'PENDENTE'
 WHERE id = 6 AND contestacao_requerente IS NULL;
