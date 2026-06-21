# Relatório U1 — Resolução de Contestação com Ajuste de Valor

## O que foi construído

### Task 1 — Domínio (enums + Contestacao + Cobranca + testes unitários)

**Arquivos modificados:**
- `dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/StatusContestacao.java`
  - Substituiu `RESOLVIDA` por `DEFERIDA, INDEFERIDA`
- `dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/Contestacao.java`
  - Removeu `resolver(String)`, adicionou `deferir(String)`, `indeferir(String)` e o privado `resolverComo(StatusContestacao, String)`
- `dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/Cobranca.java`
  - Removeu `resolverContestacao(String)`, adicionou `indeferirContestacao(String)`, `deferirContestacao(ModoAjuste, BigDecimal, String)` e o privado `calcularValorDeferido(ModoAjuste, BigDecimal)`
- `dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/CobrancaTest.java`
  - Substituiu `resolverContestacao_deveMudarStatusContestacaoParaResolvida` por 8 novos testes

**Arquivo criado:**
- `dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/ModoAjuste.java`
  - Enum `PERCENTUAL, VALOR`

### Task 2 — CobrancaServico + BDD

**Arquivos modificados:**
- `dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/CobrancaServico.java`
  - Substituiu `resolverContestacao` por `deferirContestacao(CobrancaId, ModoAjuste, BigDecimal, String)` e `indeferirContestacao(CobrancaId, String)`
  - Adicionou import `school.cesar.acadlab.dominio.gestaofinanceira.cobranca.ModoAjuste`
- `dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/resolver_contestacao.feature`
  - Reescrito com 5 cenários (indeferir, deferir percentual, deferir valor absoluto, sem contestação, segunda resolução)
- `dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/ResolverContestacaoFuncionalidade.java`
  - Completamente reescrito com os novos passos

## Evidência TDD RED/GREEN

### Task 1 — RED (compilação falha)
```
mvn -q -pl dominio-gestao-financeira test -Dtest=CobrancaTest
→ [ERROR] cannot find symbol: variable ModoAjuste
→ [ERROR] cannot find symbol: method indeferirContestacao(java.lang.String)
(falha de compilação confirmada)
```

### Task 1 — GREEN
```
mvn -q -pl dominio-gestao-financeira test -Dtest=CobrancaTest
→ Tests run: 21, Failures: 0, Errors: 0, Skipped: 0
(13 testes originais + 8 novos = 21)
```

### Task 2 — RED (após reescrever feature, antes de atualizar servico/steps)
Atualização do `CobrancaServico` e steps foram necessárias para compilar; a feature havia sido reescrita primeiro, causando falha de compilação nos steps que ainda referenciavam `resolverContestacao`.

### Gate Final — ALL GREEN
```
mvn -pl dominio-gestao-financeira test
→ Tests run: 67, Failures: 0, Errors: 0, Skipped: 0
→ BUILD SUCCESS
```

## Achados / Desvios do brief

### Desvio 1 — Locale pt_BR quebra `{double}` no Cucumber

O brief especifica `{double}` para os passos de valor monetário (e.g. `"o valor atual da cobrança permanece {double}"` e `"o setor financeiro defere a contestação com o valor {double}"`) e usa `BigDecimal.valueOf(valor)` para conversão.

**Problema descoberto:** O ambiente JVM usa locale `pt_BR` (user.language=pt, user.country=BR), onde o separador de milhar é `.` e o decimal é `,`. O Cucumber `{double}` usa `NumberFormat.getInstance()` (locale-sensível), então `1500.00` é interpretado como `150000.0`, e `1000.00` como `100000.0`.

**Impacto observado:**
- `"Indeferir mantém o valor"`: expected `1500.00`, actual < 1500.00 → `compareTo` retornou 1 em vez de 0
- `"Deferir com percentual"`: idem com `1200.00`
- `"Deferir com valor absoluto"`: `deferirContestacao` chamado com `100000.00` (em vez de `1000.00`), que é maior que `valorAtual=1500.00`, violando a invariante → `IllegalArgumentException`

**Solução aplicada:** Substituído `{double}` por `{string}` (quoted) no passo de valor na feature e nos steps:
- Feature: `permanece 1500.00` → `permanece "1500.00"` (e idem para 1200.00, 1000.00)
- Feature: `com o valor 1000.00 e` → `com o valor "1000.00" e`
- Steps: `valorAtualPermanece(double)` → `valorAtualPermanece(String)` com `new BigDecimal(valor)`
- Steps: `defereValor(double, String)` → `defereValor(String, String)` com `new BigDecimal(valor)`

Isso é consistente com o estilo usado no restante do codebase (e.g. `AplicarDescontoComBolsaSteps` usa `{double}` mas chama `BigDecimal.valueOf(valor)` — se esse módulo também rodar em pt_BR, há um risco latente nos outros steps de desconto, mas eles já passavam antes e não eram meu escopo).

### Confirmação: criarCobranca() usa valorBase 1500.00
O helper `criarCobranca()` em `CobrancaTest` cria com `new BigDecimal("1500.00")`, conforme esperado pelo brief. Os valores esperados (1200.00 para 20%, 750.00 para 50%) estão corretos.

## Resumo final

**Tests run: 67, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS**
- 21 testes unitários `CobrancaTest` (13 originais + 8 novos)
- 27 cenários Cucumber `RunCucumberTest` (todos passing, incluindo 5 novos de resolução + demais features de cobrança + features de bolsa do Sub-A)
- Total módulo: 67 testes

## U1 fix wave

### Arquivos modificados

**`dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/Cobranca.java`**
- Fix 1: Adicionado `notNull(parecer, "parecer obrigatório")` ao topo de `indeferirContestacao(String)`, antes do guard `if (contestacao == null)`. Alinha o comportamento com `deferirContestacao` (que já guardava seus parâmetros com `notNull`).

**`dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/CobrancaServico.java`**
- Fix 2: Removida a linha `import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.ModoAjuste;` (redundante; já coberta pelo wildcard `import ...cobranca.*;` imediatamente acima). `ModoAjuste` permanece resolvível via wildcard.

**`dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/CobrancaTest.java`**
- Fix 3: Método `resolverContestacaoInexistente_rejeitado` renomeado para `indeferirContestacaoInexistente_rejeitado`. Corpo inalterado (já chamava `indeferirContestacao`).
- Fix 4: Adicionado teste `deferirContestacao_valorAbsoluto_exatamente50pct_aceito` — verifica que o modo VALOR aceita exatamente 50% do valorBase (750.00 de 1500.00, o limite inclusivo). `criarCobranca()` confirmado com `valorBase = 1500.00`, portanto valor 750.00 está correto.

### Resultados de verificação

**CobrancaTest somente:**
```
mvn -q -pl dominio-gestao-financeira test -Dtest=CobrancaTest
→ (sem output = sem falhas) — todos os 22 testes GREEN
```

**Suite completa do módulo:**
```
mvn -pl dominio-gestao-financeira test
→ Tests run: 68, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS
```
(68 = 22 unitários CobrancaTest + 27 cenários Cucumber + outros unitários do módulo; +1 em relação ao baseline de 67 pelo novo teste de boundary)
