# SDD Progress Ledger — F-13 Sub-A (Bolsas & Descontos)

Plan: `docs/superpowers/plans/2026-06-21-f13-subA-bolsas.md`
Methodology: subagent-driven-development (opção 1). No commits (user instruction) — diffs from working tree vs `.superpowers/sdd/baseline/subA/` snapshots + full content of new files.
Models: implementers/reviewers = sonnet; final whole-branch review = opus.

## Units
- **U1 = Domínio** (Tasks 1-3): agregado Bolsa + enums + eventos + BolsaTest; BolsaRepositorio/BolsaServico + 3 features BDD; RN5 AutorizacaoDescontoPorBolsa + feature.
- **U2 = App + Infra + Controller** (Tasks 4-7): BolsaResumo/Repositorio/Servico aplicação; BolsaJpa + V5 seed; adapter VerificadorMatriculaConfirmadaJpa + finder; beans em BackendAplicacao (inclui CobrancaServico) + BolsaControlador.
- **U3 = Frontend** (Tasks 8-9): estender financeiro.ts + formatValidade; religar aba "Bolsas & Descontos" em financeiro.tsx.

## Baseline snapshots (pre-Sub-A)
`.superpowers/sdd/baseline/subA/`: BackendAplicacao.java.base, MatriculaJpa.java.base, financeiro.ts.base, format.ts.base, financeiro.tsx.base

## Status
- [x] U1 — complete (14 new files in dominio-gestao-financeira/.../bolsa/; 58 tests green; review clean / Approved)
- [x] U2 — complete (7 new files + 2 modified [MatriculaJpa, BackendAplicacao]; 3 compile gates green; domain tests still pass; review clean / Approved)
- [x] U3 — complete (3 files modified [format.ts, financeiro.ts, financeiro.tsx]; npm build green client+SSR; aesthetic invariance verified hunk-by-hunk; review clean / Approved)
- [x] Final whole-branch review (opus) — verdict "With fixes". Architecture/bean-wiring/schema-seed/end-to-end contracts all correct; backend will boot; listing round-trips. 2 Important findings in the "Nova concessão" form (both touch the no-aesthetic-change rule — pending user decision): #1 Matrícula field feeds estudanteId via Number() with misleading 2024.XXXX placeholder → create fails on realistic input; #2 Tipo free-text Input + cast → backend TipoBolsa.valueOf throws 500 on non-exact value. Minors #3 renovar endpoint only solicita (by design, no completar path); #4 EM_RENOVACAO is suspendível (intentional?); #5 RN5 checks status only, not validade expiry (matches plan).

## Minor findings roll-up (for final review triage)
- [U1 M1] `Bolsa.renovar` still uses commons-lang3 `notNull(novaValidade)` → throws NPE not IAE, inconsistent with the constructor `validade` fix. One-line fix: explicit `if (novaValidade == null) throw new IllegalArgumentException(...)`. (`Bolsa.java`)
- [U1 M2] `aplicar_desconto_com_bolsa.feature` scenario 2 opens with `E` (valid in Cucumber, but unusual). Cosmetic.
- [U1 M3] `BolsaFuncionalidade.excecao` field declared but unused in this unit. Cosmetic/dead field.
- [U1 I1] `Bolsa.renovar` permits renewing an ATIVA bolsa without prior `solicitarRenovacao` — brief-mandated (BolsaTest writes it this way); state machine not strict. Spec-compliant; flag if later units depend on strictness.
- [U1 I2] constructor `notNull(status)` throws NPE not IAE — inconsistent with `validade` fix; status never null in practice.
- [U3 M1] `financeiro.tsx` "Tipo" form field is a free-text `Input` with `as TipoBolsa` cast — no guard against invalid enum. A `<select>` (same FormField wrapper) would be safer. Brief-allowed; UX/data-quality only.
- [U3 M2] `financeiro.tsx` "Nova concessão" form: the "Matrícula" field (placeholder still `2024.XXXX`) now feeds numeric `estudanteId` via `Number(...)`. A formatted matrícula yields a wrong/non-integer id. Brief-introduced ambiguity. Fix: relabel/placeholder to a numeric estudante id, or add an integer guard before mutate. **Worth user attention — functional.**

## Final-review fixes (user chose: minimal hardening, no visual restructure)
- #1 FIXED: financeiro.tsx field relabeled "Matrícula"→"Estudante (ID)", placeholder→"1"; guard `!Number.isInteger(Number(...))` → toast.error + return before mutate.
- #2 FIXED: tipo normalized `trim().toUpperCase()`, validated ∈ {PROUNI,FIES,MERITO,CONVENIO} → toast.error + return; sends tipoNorm. Input kept (no select).
- Verified by controller: build green; changes confined to concession form (label/placeholder + 2 guards in submit handler).
- Minors #3-#5 + U1/U2/U3 minors left as-is (by-design / plan-faithful / cosmetic).

## SUB-A COMPLETE — all uncommitted in working tree (per user no-commit rule). Live testing is the user's.

## Log
- (init) ledger created, baselines snapshotted.
- U1: implementer DONE_WITH_CONCERNS (2 sound brief-defect fixes: validade IAE, Cucumber duplicate-step). Reviewer Spec ✅ / Approved. 58 tests green. Complete.
- U2: implementer DONE (no duplicate beans; CobrancaServico now wired). Reviewer Spec ✅ / Approved. Compile gates green. Minors are pre-existing patterns (JPA implicit no-arg ctor; proximoId COALESCE+1 race — same as MatriculaJpa). Complete.
- U3: implementer DONE. Reviewer Spec ✅ / Approved. npm build green. Aesthetic invariance verified (only bolsas tab + imports + mock removal changed). 2 Minors (Tipo free-text; matrícula→estudanteId placeholder). Complete.
