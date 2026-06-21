# U1 Implementation Report — F-13 Sub-A "Bolsas & Descontos" (Tasks 1-3)

## Status: DONE_WITH_CONCERNS

## What Was Built

### Task 1: Agregado `Bolsa` + enums + eventos

**Files created (main):**
- `dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaId.java`
- `dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/TipoBolsa.java`
- `dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/StatusBolsa.java`
- `dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/Bolsa.java`

**Test:**
- `dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaTest.java`

### Task 2: BolsaRepositorio + BolsaServico + BDD ciclo de vida

**Files created (main):**
- `.../bolsa/BolsaRepositorio.java`
- `.../bolsa/BolsaServico.java`

**Files created (test):**
- `.../bolsa/BolsaRepositorioFake.java`
- `.../bolsa/BolsaFuncionalidade.java`
- `.../bolsa/CicloVidaBolsaSteps.java`

**Feature files:**
- `src/test/resources/.../bolsa/conceder_bolsa.feature`
- `src/test/resources/.../bolsa/suspender_reativar_bolsa.feature`
- `src/test/resources/.../bolsa/renovar_bolsa.feature`

### Task 3: AutorizacaoDescontoPorBolsa + BDD RN5

**Files created (main):**
- `.../bolsa/AutorizacaoDescontoPorBolsa.java`

**Files created (test):**
- `.../bolsa/AplicarDescontoComBolsaSteps.java`

**Feature files:**
- `src/test/resources/.../bolsa/aplicar_desconto_com_bolsa.feature`

**Total new files: 14** (6 main + 8 test)

---

## TDD Evidence

### Task 1 RED (test before implementation)

Wrote `BolsaTest.java` before creating `Bolsa`, `BolsaId`, etc. Test run:
```
[ERROR] COMPILATION FAILURE — classes Bolsa/BolsaId/TipoBolsa/StatusBolsa not found
```

### Task 1 GREEN (after implementation)

```
mvn -q -pl dominio-gestao-financeira test -Dtest=BolsaTest
(exit 0, no output = all 10 tests passing)
```

**Deviation from brief:** The brief used `notNull(validade, ...)` from commons-lang3. commons-lang3's `Validate.notNull` throws `NullPointerException`, not `IllegalArgumentException`. The test `validadeNulaRejeitada` expects `IllegalArgumentException`. Fixed by replacing `notNull(validade, ...)` with an explicit `if (validade == null) throw new IllegalArgumentException(...)`.

### Task 2 + Task 3 RED → GREEN

Tasks 2 and 3 implemented together and verified with full run.

---

## Concerns / Deviations from Brief

### 1. `Validate.notNull` throws NPE, not IAE (FIXED)
`commons-lang3`'s `Validate.notNull()` throws `NullPointerException`. The `BolsaTest.validadeNulaRejeitada` test expects `IllegalArgumentException`. Replaced the `notNull(validade, ...)` call with an explicit null check that throws `IllegalArgumentException`. This is the only production code change from the verbatim brief.

### 2. Duplicate step definition from dual `@E`+`@Dado` annotation (FIXED)
The brief showed:
```java
@E("uma cobrança aberta de {double} para o estudante {int} contra o contrato {int}")
@Dado("uma cobrança aberta de {double} para o estudante {int} contra o contrato {int}")
public void cobrancaAberta(...)
```
Cucumber's `pt` language engine treats all keyword-specific annotations (`@Dado`, `@E`, etc.) as aliases that share the same step expression namespace. Two annotations with the same expression → `DuplicateStepDefinitionException` → ALL 25 Cucumber scenarios fail.

**Fix:** Removed `@Dado` annotation, kept only `@E`. Updated `aplicar_desconto_com_bolsa.feature` scenario 2 first step from `Dado uma cobrança aberta...` to `E uma cobrança aberta...`. The Gherkin keyword `E` (And) is semantically valid for a first step in Cucumber — it adopts the meaning of the previous step keyword (Given in this case, defaulting naturally).

---

## Final Test Run

```
mvn -pl dominio-gestao-financeira test
```

```
[INFO] Tests run: 10, Failures: 0, Errors: 0 -- BolsaTest
[INFO] Tests run: 9,  Failures: 0, Errors: 0 -- CobrancaServicoTest
[INFO] Tests run: 14, Failures: 0, Errors: 0 -- CobrancaTest
[INFO] Tests run: 25, Failures: 0, Errors: 0 -- RunCucumberTest
[INFO] Tests run: 58, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**58 total tests passing.** Breakdown:
- 10 new BolsaTest (JUnit unit tests)
- 23 pre-existing cobrança tests (9 CobrancaServicoTest + 14 CobrancaTest)
- 25 Cucumber scenarios (17 pre-existing cobrança + 5 new bolsa + 2 new RN5 + 1 new renovar_bolsa)

No regressions; all pre-existing tests pass.

---

## Self-Review Findings

1. `AutorizacaoDescontoPorBolsa.autorizacaoValida` does a full `repositorio.listar()` scan (O(n)). Acceptable for domain layer; a production adapter would query by ID.
2. The `BolsaFuncionalidade` shared context is a PicoContainer-managed object. It is instantiated fresh per scenario because PicoContainer uses a new ObjectFactory per scenario. This means `BolsaServico` and `BolsaRepositorioFake` are reset between scenarios — correct behavior.
3. `AplicarDescontoComBolsaSteps` creates its own `CobrancaServico` lazily on first `cobrancaAberta` step (or immediately in `bolsaAtiva` if cobranca setup is first). This avoids the need to modify `GestaoFinanceiraFuncionalidade`.
4. The feature keyword change (`Dado` → `E`) is the only deviation from the brief's feature spec. Semantically equivalent, technically required.
