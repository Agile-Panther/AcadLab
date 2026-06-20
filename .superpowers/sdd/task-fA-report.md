# Task fA Report — F-13 Sub-projeto 0 Backend

## 1. Status

DONE

## 2. Files Created/Modified

### Created
- `aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/PagamentoResumo.java`
- `aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/ContestacaoResumo.java`
- `aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/DescontoResumo.java`

### Modified
- `aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/CobrancaResumo.java` — extended with `pagamento`, `contestacao`, `descontos` fields
- `aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/CobrancaRepositorioAplicacao.java` — added `pesquisarContestacoesAbertas()` method signature
- `aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/CobrancaServicoAplicacao.java` — added `pesquisarContestacoesAbertas()` delegation
- `infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/CobrancaJpa.java` — added imports for sub-DTOs; replaced inline mapping in `pesquisarPorContrato(int)` with `toResumo` helper; added `pesquisarContestacoesAbertas()` impl; added `findByContestacaoStatus(StatusContestacao)` to `CobrancaJpaRepository`
- `apresentacao-backend/src/main/java/school/cesar/acadlab/apresentacao/gestaofinanceira/CobrancaControlador.java` — added `GET contestacoes-abertas` endpoint

## 3. Verification Results

### Command 1: `mvn -pl apresentacao-backend -am compile`
```
[INFO] BUILD SUCCESS
```

### Command 2: `mvn -pl dominio-gestao-financeira test`
```
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0 -- CobrancaServicoTest
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0 -- CobrancaTest
[INFO] Tests run: 19, Failures: 0, Errors: 0, Skipped: 0 -- RunCucumberTest
[INFO] Tests run: 42, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
All 42 tests (including the 8 BDD features) remain green.

## 4. Concerns / Deviations

None. The implementation follows the plan exactly:
- `toResumo` is a private helper reused by both `pesquisarPorContrato(int)` and `pesquisarContestacoesAbertas()`.
- No domain entity is serialized as JSON (only record DTOs).
- `CobrancaRepositorioImpl` accesses JPA fields directly (same file/package, package-private access).
- `StatusContestacao` was already imported in `CobrancaJpa.java`, so `findByContestacaoStatus(StatusContestacao.PENDENTE)` compiles without additional imports.

## 5. Self-Review

**Added beyond the plan:** Nothing extra.

**Missed from the plan:** Nothing. Tasks 3-5 (frontend) were explicitly excluded per instructions.

**DRY check:** `toResumo` is called from both `pesquisarPorContrato` and `pesquisarContestacoesAbertas` — confirmed DRY.

**Serialization check:** `CobrancaControlador.emitirComprovante` returns a `Pagamento` domain entity directly (pre-existing issue, not introduced here — it was in the existing code before this task).
