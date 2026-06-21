# U2 Implementation Report — Infra + Controller

## Files Modified

### `infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/CobrancaJpa.java`
- Replaced the reconstitution block (lines 219-225) that referenced the removed `StatusContestacao.RESOLVIDA` and `contestacao.resolver(...)`.
- New block branches on `DEFERIDA` → `contestacao.deferir(parecer)` and `INDEFERIDA` → `contestacao.indeferir(parecer)`. PENDENTE remains no-op (Contestacao stays in initial state). `valorAtual` reconstruction is untouched (handled separately in `Cobranca.reconstituir`).

### `apresentacao-backend/src/main/java/school/cesar/acadlab/apresentacao/gestaofinanceira/CobrancaControlador.java`
- Removed single `resolverContestacao` endpoint (POST `{id}/resolver-contestacao`).
- Added `deferirContestacao` (POST `{id}/deferir-contestacao`, body `DeferirContestacaoRequest{modo, valor, parecer}`) calling `servico.deferirContestacao`.
- Added `indeferirContestacao` (POST `{id}/indeferir-contestacao`, body `IndeferirContestacaoRequest{parecer}`) calling `servico.indeferirContestacao`.
- Added import `school.cesar.acadlab.dominio.gestaofinanceira.cobranca.ModoAjuste`.
- Added records `DeferirContestacaoRequest(String modo, BigDecimal valor, String parecer)` and `IndeferirContestacaoRequest(String parecer)` at end of class.

## Files Created

### `apresentacao-backend/src/main/resources/db/migration/V6__contestacao_seed.sql`
- Populates missing contestacao columns for cobrança id=6 (already `CONTESTADA` in V1 but columns were NULL).
- Sets `contestacao_requerente=3`, justificativa, data `2025-08-15`, status `PENDENTE`.
- Uses guarded `WHERE id = 6 AND contestacao_requerente IS NULL` to be idempotent.

## Cobrança 6 estudante_id Verification
V1 seed line: `(6, 3, 3, 1, 2500.00, 2500.00, '2025-08-10', 1, 'CONTESTADA')` — format is `(id, contrato_id, estudante_id, ...)`.
- `estudante_id = 3` ✓ matches brief assumption. `contestacao_requerente = 3` is correct; titularidade invariant respected.

## Compile Results

### Task 3: `mvn -q -pl infraestrutura -am compile`
- Result: **BUILD SUCCESS** (no output = success in quiet mode)

### Task 4: `mvn -q -pl apresentacao-backend -am compile`
- Result: **BUILD SUCCESS** (full chain: domínio + aplicação + infraestrutura + apresentacao-backend all compile)

## Domain Test Re-run
Command: `mvn -pl dominio-gestao-financeira test`
- Result: **Tests run: 68, Failures: 0, Errors: 0, Skipped: 0** — BUILD SUCCESS
- No regression from U1 domain layer.

## Self-Review

- Reconstitution correctly handles all three live statuses: PENDENTE (no extra call), DEFERIDA (deferir), INDEFERIDA (indeferir). The old RESOLVIDA is gone.
- Controller uses `ModoAjuste.valueOf(request.modo())` — runtime string→enum conversion; standard pattern and consistent with the brief.
- No domain entity is serialized as JSON; both new endpoints use `record` DTOs.
- V6 migration is safe: idempotent guard prevents double-apply, V1 is untouched.
- `BigDecimal` was already imported in the controller (used by `GerarCobrancaRequest`); no duplicate import.

## Concerns
None. Everything compiled and tests pass cleanly.
