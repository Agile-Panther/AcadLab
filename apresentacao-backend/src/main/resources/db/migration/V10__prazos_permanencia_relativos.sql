-- =============================================================================
-- V10__prazos_permanencia_relativos.sql
-- Reposiciona os prazos dos editais/benefícios de demonstração relativos a
-- CURRENT_DATE, para que TODO o fluxo da tela de Permanência Acadêmica
-- (inscrever, interpor recurso, solicitar renovação, publicar resultado,
-- encerrar) fique sempre dentro do prazo, independentemente da data em que a
-- demonstração for executada.
--
-- O backend valida tanto o status quanto a janela de datas (Edital.isInscricaoAberta,
-- Edital.isRecursoAberto, Edital.publicarResultado, Beneficio.solicitarRenovacao).
-- Como o seed (V1/V2) usava datas fixas de 2025, qualquer execução posterior
-- recaía em "fora do prazo".
-- =============================================================================

-- ─── EDITAIS COM INSCRIÇÕES ABERTAS (1 e 2) ──────────────────────────────────
-- Janela de inscrição abrange o dia atual; recursos logo após o encerramento.
UPDATE edital_permanencia
   SET prazo_inscricao_inicio = CURRENT_DATE - 15,
       prazo_inscricao_fim    = CURRENT_DATE + 45,
       prazo_recurso_inicio   = CURRENT_DATE + 46,
       prazo_recurso_fim      = CURRENT_DATE + 60,
       status                 = 'INSCRICOES_ABERTAS'
 WHERE id IN (1, 2);

-- prazo de renovação do edital 1 no futuro (edital 2 não prevê renovação: null)
UPDATE edital_permanencia
   SET prazo_renovacao = CURRENT_DATE + 180
 WHERE id = 1;

-- ─── EDITAL ENCERRADO (3) — RECURSO AINDA EM PRAZO ───────────────────────────
-- Inscrições já fechadas (exemplo de edital encerrado), porém a janela de
-- recursos abrange o dia atual: permite demonstrar "Interpor recurso" sobre a
-- inscrição INDEFERIDA (id=1) do estudante 1 neste edital.
UPDATE edital_permanencia
   SET prazo_inscricao_inicio = CURRENT_DATE - 90,
       prazo_inscricao_fim    = CURRENT_DATE - 30,
       prazo_recurso_inicio   = CURRENT_DATE - 3,
       prazo_recurso_fim      = CURRENT_DATE + 14
 WHERE id = 3;

-- ─── EDITAL PARA O CICLO "PUBLICAR RESULTADO → ENCERRAR" (4) ──────────────────
-- Inscrições e recursos já encerrados, mas resultado ainda não publicado:
-- estado em que a Assistência Estudantil pode publicar o resultado e, em
-- seguida, encerrar o edital. Sem inscrições vinculadas (não interfere nas demais).
INSERT INTO edital_permanencia (id, programa, descricao, vagas,
                                prazo_inscricao_inicio, prazo_inscricao_fim,
                                prazo_recurso_inicio,   prazo_recurso_fim,
                                prazo_renovacao, status) VALUES
  (4, 'Auxílio Transporte 2025.1',
   'Auxílio para custeio de deslocamento de estudantes em vulnerabilidade socioeconômica.',
   40,
   CURRENT_DATE - 120, CURRENT_DATE - 60,
   CURRENT_DATE - 50,  CURRENT_DATE - 40,
   null, 'INSCRICOES_ABERTAS')
ON CONFLICT (id) DO NOTHING;

-- ─── BENEFÍCIOS — JANELA DE RENOVAÇÃO ABERTA ─────────────────────────────────
-- Coloca o prazo de renovação do benefício ativo do estudante 1 dentro da janela
-- de renovação (≤ 90 dias do prazo), para demonstrar "Solicitar renovação".
UPDATE beneficio_permanencia
   SET prazo_renovacao = CURRENT_DATE + 30,
       solicitou_renovacao = false
 WHERE id = 1;
