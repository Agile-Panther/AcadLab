# F-09 Integração — Progress Ledger

Branch: main (working tree, NO commits per user instruction)
Plan: docs/superpowers/plans/2026-06-21-f09-integracao-frontend-backend.md

## Units
- Unit A (Backend, Tasks 1-2): COMPLETE (working tree, no commit; compile+34 tests green; review clean)
- Unit B (Frontend, Tasks 3-5): COMPLETE (working tree, no commit; npm build green; review clean; unused hojeIso import removed)
- Final review: COMPLETE (opus). 1 Important bug found & FIXED: consultarSaldo serialized Map<CategoriaAtividadeId,Integer> via record toString() → frontend saldo[id] always 0. Fixed to Map<Integer,Integer> keyed by id.valor(); backend recompiles green.

## Minor findings rollup
- Unit A Minor: @Table CATEGORIA_ATIVIDADE vs seed categoria_atividade — confirmed safe (PG case-folding + Spring snake_case strategy, consistent w/ CobrancaJpa). No action.

## STATUS: F-09 COMPLETE (working tree, uncommitted)
All F-09 changes applied to main working tree. Verified: mvn compile (backend) green, dominio-atividades-complementares 34 tests green, npm run build green. No commits per user rule.
