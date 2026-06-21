### Task 1: Domínio — enums + `Contestacao` + agregado `Cobranca` (com testes unitários)

**Files:**
- Modify: `dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/StatusContestacao.java`
- Create: `dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/ModoAjuste.java`
- Modify: `dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/Contestacao.java`
- Modify: `dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/Cobranca.java:59-65` (remove `resolverContestacao`, add `deferirContestacao`/`indeferirContestacao`)
- Test: `dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/CobrancaTest.java:56-63` (substituir o teste de resolução)

**Interfaces:**
- Produces (consumido Tasks 2-6): `StatusContestacao{PENDENTE,DEFERIDA,INDEFERIDA}`; `ModoAjuste{PERCENTUAL,VALOR}`; `Contestacao.deferir(String)`, `Contestacao.indeferir(String)` (de PENDENTE; senão ISE); `Cobranca.indeferirContestacao(String)→ContestacaoResolvidaEvento`, `Cobranca.deferirContestacao(ModoAjuste,BigDecimal,String)→ContestacaoResolvidaEvento`.

- [ ] **Step 1: Atualizar os testes unitários de `CobrancaTest` (RED)**

Em `CobrancaTest.java`, **substituir** o método `resolverContestacao_deveMudarStatusContestacaoParaResolvida` (linhas 56-63) por:

```java
    @Test
    void indeferirContestacao_mantemValorEMarcaIndeferida() {
        var cobranca = criarCobranca();
        var valorAntes = cobranca.getValorAtual();
        cobranca.contestar(new EstudanteId(1), "motivo", LocalDate.now());
        cobranca.indeferirContestacao("Cobrança correta");
        assertEquals(StatusContestacao.INDEFERIDA, cobranca.getContestacao().getStatus());
        assertEquals(StatusCobranca.ABERTA, cobranca.getStatus());
        assertEquals(0, valorAntes.compareTo(cobranca.getValorAtual()));
    }

    @Test
    void deferirContestacao_percentual_reduzValorEIncrementaVersao() {
        var cobranca = criarCobranca(); // valorBase 1500.00
        int versaoAntes = cobranca.getVersao();
        cobranca.contestar(new EstudanteId(1), "motivo", LocalDate.now());
        cobranca.deferirContestacao(ModoAjuste.PERCENTUAL, new BigDecimal("20"), "Deferido parcial");
        assertEquals(StatusContestacao.DEFERIDA, cobranca.getContestacao().getStatus());
        assertEquals(StatusCobranca.ABERTA, cobranca.getStatus());
        assertEquals(0, new BigDecimal("1200.00").compareTo(cobranca.getValorAtual()));
        assertEquals(versaoAntes + 1, cobranca.getVersao());
    }

    @Test
    void deferirContestacao_percentualMaximo50() {
        var cobranca = criarCobranca(); // 1500.00
        cobranca.contestar(new EstudanteId(1), "motivo", LocalDate.now());
        cobranca.deferirContestacao(ModoAjuste.PERCENTUAL, new BigDecimal("50"), "Metade");
        assertEquals(0, new BigDecimal("750.00").compareTo(cobranca.getValorAtual()));
    }

    @Test
    void deferirContestacao_percentualInvalido_rejeitado() {
        var cobranca = criarCobranca();
        cobranca.contestar(new EstudanteId(1), "motivo", LocalDate.now());
        assertThrows(IllegalArgumentException.class, () ->
                cobranca.deferirContestacao(ModoAjuste.PERCENTUAL, new BigDecimal("55"), "x"));
        var outra = criarCobranca();
        outra.contestar(new EstudanteId(1), "motivo", LocalDate.now());
        assertThrows(IllegalArgumentException.class, () ->
                outra.deferirContestacao(ModoAjuste.PERCENTUAL, new BigDecimal("7"), "x"));
    }

    @Test
    void deferirContestacao_valorAbsoluto_defineValor() {
        var cobranca = criarCobranca(); // 1500.00
        cobranca.contestar(new EstudanteId(1), "motivo", LocalDate.now());
        cobranca.deferirContestacao(ModoAjuste.VALOR, new BigDecimal("1000.00"), "Novo valor");
        assertEquals(0, new BigDecimal("1000.00").compareTo(cobranca.getValorAtual()));
    }

    @Test
    void deferirContestacao_valorAbaixoDe50pct_rejeitado() {
        var cobranca = criarCobranca(); // 1500.00 → mínimo 750.00
        cobranca.contestar(new EstudanteId(1), "motivo", LocalDate.now());
        assertThrows(IllegalArgumentException.class, () ->
                cobranca.deferirContestacao(ModoAjuste.VALOR, new BigDecimal("700.00"), "x"));
    }

    @Test
    void resolverContestacaoInexistente_rejeitado() {
        var cobranca = criarCobranca();
        assertThrows(IllegalStateException.class, () -> cobranca.indeferirContestacao("x"));
    }

    @Test
    void resolverDuasVezes_rejeitado() {
        var cobranca = criarCobranca();
        cobranca.contestar(new EstudanteId(1), "motivo", LocalDate.now());
        cobranca.indeferirContestacao("primeira");
        assertThrows(IllegalStateException.class, () -> cobranca.deferirContestacao(
                ModoAjuste.PERCENTUAL, new BigDecimal("10"), "segunda"));
    }
```

Confirmar que `CobrancaTest` importa `ModoAjuste` (mesmo pacote `cobranca`, sem import) e que `criarCobranca()` cria com `valorBase` 1500.00 — se o helper usar outro valor, ajustar os valores esperados proporcionalmente.

- [ ] **Step 2: Rodar e ver falhar (compilação)**

Run: `mvn -q -pl dominio-gestao-financeira test -Dtest=CobrancaTest`
Expected: FALHA de compilação (`ModoAjuste`, `deferirContestacao`, `indeferirContestacao`, `StatusContestacao.DEFERIDA/INDEFERIDA` inexistentes).

- [ ] **Step 3: Atualizar `StatusContestacao`**

```java
package school.cesar.acadlab.dominio.gestaofinanceira;

public enum StatusContestacao {
    PENDENTE, DEFERIDA, INDEFERIDA
}
```

- [ ] **Step 4: Criar `ModoAjuste`**

`ModoAjuste.java`:
```java
package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

public enum ModoAjuste { PERCENTUAL, VALOR }
```

- [ ] **Step 5: Atualizar `Contestacao` (deferir/indeferir no lugar de resolver)**

Substituir o método `resolver` (linhas 25-31) por:
```java
    public void deferir(String parecer) {
        resolverComo(StatusContestacao.DEFERIDA, parecer);
    }

    public void indeferir(String parecer) {
        resolverComo(StatusContestacao.INDEFERIDA, parecer);
    }

    private void resolverComo(StatusContestacao novoStatus, String parecer) {
        notNull(parecer, "parecer obrigatório");
        if (status != StatusContestacao.PENDENTE)
            throw new IllegalStateException("contestação já foi resolvida");
        this.status = novoStatus;
        this.parecer = parecer;
    }
```

- [ ] **Step 6: Atualizar `Cobranca` (deferir/indeferir + ajuste de valor)**

Em `Cobranca.java`, **substituir** `resolverContestacao` (linhas 59-65) por:
```java
    public ContestacaoResolvidaEvento indeferirContestacao(String parecer) {
        if (contestacao == null)
            throw new IllegalStateException("não há contestação registrada");
        contestacao.indeferir(parecer);
        this.status = StatusCobranca.ABERTA;
        return new ContestacaoResolvidaEvento(this);
    }

    public ContestacaoResolvidaEvento deferirContestacao(ModoAjuste modo, BigDecimal valor, String parecer) {
        if (contestacao == null)
            throw new IllegalStateException("não há contestação registrada");
        notNull(modo, "modo obrigatório");
        notNull(valor, "valor obrigatório");
        var novoValor = calcularValorDeferido(modo, valor);
        this.historico.add(new HistoricoVersao(this.versao, this.valorAtual, "Contestação deferida", LocalDate.now()));
        this.versao++;
        this.valorAtual = novoValor;
        contestacao.deferir(parecer);
        this.status = StatusCobranca.ABERTA;
        return new ContestacaoResolvidaEvento(this);
    }

    private BigDecimal calcularValorDeferido(ModoAjuste modo, BigDecimal valor) {
        if (modo == ModoAjuste.PERCENTUAL) {
            var cinco = new BigDecimal("5");
            isTrue(valor.compareTo(cinco) >= 0 && valor.compareTo(new BigDecimal("50")) <= 0
                    && valor.remainder(cinco).compareTo(BigDecimal.ZERO) == 0,
                    "percentual deve ser múltiplo de 5 entre 5 e 50");
            var fator = BigDecimal.ONE.subtract(valor.divide(new BigDecimal("100"), MathContext.DECIMAL64));
            return this.valorAtual.multiply(fator).setScale(2, java.math.RoundingMode.HALF_UP);
        }
        var minimo = this.valorAtual.multiply(new BigDecimal("0.5"));
        isTrue(valor.compareTo(minimo) >= 0 && valor.compareTo(this.valorAtual) < 0,
                "valor deve reduzir no máximo 50% e ser menor que o valor atual");
        return valor.setScale(2, java.math.RoundingMode.HALF_UP);
    }
```

(`MathContext` e `isTrue`/`notNull` já estão importados no arquivo; `ModoAjuste` é do mesmo pacote. `ContestacaoResolvidaEvento` e a classe aninhada permanecem.)

- [ ] **Step 7: Rodar os testes unitários e ver passar**

Run: `mvn -q -pl dominio-gestao-financeira test -Dtest=CobrancaTest`
Expected: PASS (todos os testes de `CobrancaTest`, incluindo os novos de deferir/indeferir).

---

