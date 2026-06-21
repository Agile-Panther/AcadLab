# SDD Progress Ledger — Resolução de Contestação com Ajuste de Valor

Plan: `docs/superpowers/plans/2026-06-21-contestacao-resolucao-ajuste-valor.md`
Spec: `docs/superpowers/specs/2026-06-21-contestacao-resolucao-ajuste-valor-design.md`
Methodology: subagent-driven-development (opção 1). No commits — diffs from working tree vs `.superpowers/sdd/baseline/contestacao/` (dirty files) or `git diff HEAD` (clean tracked files) or /dev/null (new files).
Models: implementers/reviewers = sonnet; final whole-branch review = opus.

## Units
- **U1 = Domínio** (Tasks 1-2): StatusContestacao + ModoAjuste + Contestacao.deferir/indeferir + Cobranca (ajuste %/valor + auditoria) + CobrancaTest; CobrancaServico + BDD feature/steps. All clean tracked → `git diff HEAD`.
- **U2 = Infra + Controller** (Tasks 3-4): CobrancaJpa reconstituição + seed V6; CobrancaControlador endpoints deferir/indeferir. (dirty: CobrancaJpa.java, CobrancaControlador.java → baseline; V6 new → /dev/null)
- **U3 = Frontend** (Tasks 5-6): financeiro.ts tipos+hooks; financeiro.tsx aba Contestações + painel Resolver. (dirty: financeiro.ts, financeiro.tsx → baseline)

## Baseline snapshots (pre-feature)
`.superpowers/sdd/baseline/contestacao/`: CobrancaControlador.java.base, CobrancaJpa.java.base, financeiro.ts.base, financeiro.tsx.base
(domain files U1 são limpos → git diff HEAD)

## Status
- [x] U1 — complete (ModoAjuste new + 6 modified domain/test files; 68 tests green; review Spec ✅ Approved; Important guard fix applied + verified)
- [x] U2 — complete (CobrancaJpa + CobrancaControlador modified, V6 seed new; backend chain compiles; domain tests 68 green; review Spec ✅ Approved)
- [x] U3 — complete (financeiro.ts + financeiro.tsx modified; npm build green client+SSR; aesthetic invariance verified hunk-by-hunk; review Spec ✅ Approved)
- [x] Final whole-branch review (opus) — verdict "With fixes". Load-bearing tudo OK (boots, round-trip, validação consistente entre camadas, valor persiste sem dupla aplicação, seed/auditoria corretos). 1 Important UX + 2 Minors.
  - [Important — UX, decisão do usuário] `contestacoes-abertas` filtra só PENDENTE → ao resolver, a linha SAI da fila; os ramos de badge DEFERIDA/INDEFERIDA viram código morto nessa tabela. Dado NÃO se perde: valorAtual ajustado + status DEFERIDA persistem e aparecem no extrato (pesquisarPorContrato). Decisão: (A) fila esvazia + remover badges mortos; ou (B) trazer resolvidas na lista (mudança backend/DTO).
  - [Minor] audit (HistoricoVersao "Contestação deferida") persistido mas só visível no extrato, não no painel de resolução.
  - [Minor] guarda VALOR no front usa float JS (domínio é BigDecimal, autoritativo) — sem divergência observável nos valores do seed.
  - [Minor — recomendação] deferir/indeferir retornam void; poderia retornar DTO com novo valor+status p/ confirmar sem 2º round-trip.

## Minor findings roll-up (for final review triage)
- [U1] Out-of-scope orphans were EXPECTED and are U2/U4 scope: CobrancaJpa.java:223 (RESOLVIDA) + CobrancaControlador resolverContestacao call — Tasks 3-4 fix these. Cross-module build intentionally broken until U2.
- [U1 Minor] (resolved in fix wave) redundant ModoAjuste import; test rename; +exact-50%-VALOR boundary test added.
- [U2 pre-existing] CobrancaControlador.emitirComprovante retorna entidade Pagamento como JSON (viola "só DTOs") — latente do handoff, fora do escopo desta feature.
- [U3 Minor] rotuloStatusContestacao/tomStatusContestacao definidos dentro de FinanceiroView (recriados a cada render) — mover p/ módulo. Cosmético.
- [U3 Minor] toast genérico "Informe o valor do deferimento." no modo PERCENTUAL (campo é select de %) — UX nit.

## Final-review fix (user escolheu "fila esvazia")
- FIXED: removidos os helpers rotuloStatusContestacao/tomStatusContestacao (código morto) e revertida a coluna Status para o badge estático `<StatusBadge tone="info">Em análise</StatusBadge>` (lista é PENDENTE-only). Painel "Resolver" + hooks deferir/indeferir intactos. npm build verde.
- Minors deixados como-está (preferência minimal do usuário): audit visível só no extrato; guarda VALOR usa float JS (domínio BigDecimal é autoritativo); deferir/indeferir retornam void.

## FEATURE COMPLETA — tudo uncommitted na working tree (regra no-commit). Teste ao vivo é do usuário.
Gates integrados: domínio 68/68; backend compila; front builda (client+SSR).

## Log
- (init) ledger created, baselines snapshotted (4 dirty files; domain files clean).
- U1: implementer DONE_WITH_CONCERNS (sound {double}→{string} BDD locale fix for pt_BR). Reviewer Spec ✅ / both tasks Approved. Fix wave: added notNull(parecer) guard in indeferirContestacao + cosmetics + boundary test. 68 tests green. Complete.
- U2: implementer DONE (CobrancaJpa reconstituição + V6 seed cobrança 6 [estudante 3] + controller endpoints). Reviewer Spec ✅ / Approved. Backend compiles, 68 domain tests green. Pre-existing finding (not this feature): emitirComprovante serializa Pagamento (entidade) — latente conhecido do handoff. Complete.
- U3: implementer DONE. Reviewer Spec ✅ / Approved. npm build green. Aesthetic invariance OK (só aba Contestações + estado/painel). 2 Minors cosméticos. Complete.
