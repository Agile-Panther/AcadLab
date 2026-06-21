# U2 Implementation Report — Tasks 4–7 (F-13 Sub-A Bolsas & Descontos)

## Files Created

1. `aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/BolsaResumo.java`
   — Record DTO with fields (id, estudanteId, tipo, percentual, validade, status).

2. `aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/BolsaRepositorioAplicacao.java`
   — Port interface with `List<BolsaResumo> listarResumos()`.

3. `aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/BolsaServicoAplicacao.java`
   — Application service, ctor-validated, delegates to `BolsaRepositorioAplicacao`.

4. `infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/BolsaJpa.java`
   — Contains `BolsaJpa` @Entity, `BolsaJpaRepository` (with COALESCE MAX+1 query),
     and `@Repository BolsaRepositorioImpl implements BolsaRepositorio, BolsaRepositorioAplicacao`.

5. `apresentacao-backend/src/main/resources/db/migration/V5__bolsas.sql`
   — Seed migration inserting 4 bolsas with ON CONFLICT DO NOTHING.

6. `infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/VerificadorMatriculaConfirmadaJpa.java`
   — @Component implementing VerificadorMatriculaConfirmada via MatriculaJpaRepository finder.

7. `apresentacao-backend/src/main/java/school/cesar/acadlab/apresentacao/gestaofinanceira/BolsaControlador.java`
   — @RestController at `backend/bolsas` exposing GET (listar), POST conceder,
     POST {id}/suspender, POST {id}/reativar, POST {id}/renovar. Returns only DTOs.

## Files Modified

- `infraestrutura/.../jpa/MatriculaJpa.java`
  Added finder to `MatriculaJpaRepository`:
  `boolean existsByEstudanteIdAndPeriodoLetivoIdAndStatus(int, int, StatusMatricula)`
  The `StatusMatricula` import was already present — confirmed before editing.

- `apresentacao-backend/.../BackendAplicacao.java`
  Added 9 imports and 4 new @Bean methods:
  `bolsaServico`, `bolsaServicoAplicacao`, `verificadorAutorizacaoDesconto`, `cobrancaServico`.
  No pre-existing CobrancaServico bean found — the file only had CobrancaServicoAplicacao.
  No pre-existing VerificadorAutorizacaoDesconto or VerificadorMatriculaConfirmada beans found.

## Reconciliation Notes

- `CobrancaServico` was NOT previously wired in BackendAplicacao (as expected per handoff).
  The real constructor signature `(CobrancaRepositorio, VerificadorMatriculaConfirmada, VerificadorAutorizacaoDesconto, EventoBarramento)` was verified against source before wiring.
- No duplicate beans created; no placeholder beans detected.
- `BolsaControlador.renovar` uses `servico.solicitarRenovacao(new BolsaId(id))` (not `renovar`)
  as specified in the brief — matches the BolsaServico method which is appropriate for the endpoint semantics.

## Compile Results

- After Task 4: `mvn -q -pl aplicacao -am compile` → BUILD SUCCESS (no output = success)
- After Tasks 5+6: `mvn -q -pl infraestrutura -am compile` → BUILD SUCCESS
- After Task 7: `mvn -q -pl apresentacao-backend -am compile` → BUILD SUCCESS

## Domain Test Re-run

`mvn -q -pl dominio-gestao-financeira test` → All 28 Cucumber scenarios PASSED. No regression.

## Self-Review

- All controllers expose only DTO records (BolsaResumo), never domain entities. ✓
- BolsaRepositorioImpl implements both ports with distinct method names (listar vs listarResumos)
  to avoid generic erasure collision. ✓
- V5__bolsas.sql placed in apresentacao-backend/src/main/resources/db/migration/. ✓
- No git add or commit performed. ✓
- No server started, no endpoints called. ✓

## Concerns

None. Implementation straightforward, no contradictions found between briefs and real source.
