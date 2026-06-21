# Final Whole-Feature Review Package — Resolução de Contestação com Ajuste de Valor

Union of all 3 units' diffs (no commits — working tree vs pre-feature baselines / git diff HEAD / /dev/null). Sub-0/Sub-A/F-09 changes excluded.

============================================================
## UNIT 1 — DOMAIN (Tasks 1-2)
============================================================
# U1 Review Package — Contestação resolução: domínio (Tasks 1-2)

No commits. Modified files via git diff HEAD; new file (ModoAjuste) vs /dev/null.

## === NEW: ModoAjuste.java (vs /dev/null) ===
diff --git a/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/ModoAjuste.java b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/ModoAjuste.java
new file mode 100644
index 0000000..60744f8
--- /dev/null
+++ b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/ModoAjuste.java
@@ -0,0 +1,3 @@
+package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;
+
+public enum ModoAjuste { PERCENTUAL, VALOR }

## === MODIFIED (git diff HEAD) ===
diff --git a/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/CobrancaServico.java b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/CobrancaServico.java
index 91f67dd..a406e31 100644
--- a/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/CobrancaServico.java
+++ b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/CobrancaServico.java
@@ -3,6 +3,7 @@ package school.cesar.acadlab.dominio.gestaofinanceira;
 import static org.apache.commons.lang3.Validate.notNull;
 import school.cesar.acadlab.dominio.evento.EventoBarramento;
 import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.*;
+import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.ModoAjuste;
 import java.math.BigDecimal;
 import java.time.LocalDate;
 import java.util.List;
@@ -65,10 +66,18 @@ public class CobrancaServico {
         barramento.postar(evento);
     }
 
-    public void resolverContestacao(CobrancaId id, String parecer) {
+    public void deferirContestacao(CobrancaId id, ModoAjuste modo, BigDecimal valor, String parecer) {
         notNull(id, "id obrigatório");
         var cobranca = repositorio.obter(id);
-        var evento = cobranca.resolverContestacao(parecer);
+        var evento = cobranca.deferirContestacao(modo, valor, parecer);
+        repositorio.salvar(cobranca);
+        barramento.postar(evento);
+    }
+
+    public void indeferirContestacao(CobrancaId id, String parecer) {
+        notNull(id, "id obrigatório");
+        var cobranca = repositorio.obter(id);
+        var evento = cobranca.indeferirContestacao(parecer);
         repositorio.salvar(cobranca);
         barramento.postar(evento);
     }
diff --git a/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/StatusContestacao.java b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/StatusContestacao.java
index bf75111..488165c 100644
--- a/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/StatusContestacao.java
+++ b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/StatusContestacao.java
@@ -1,5 +1,5 @@
 package school.cesar.acadlab.dominio.gestaofinanceira;
 
 public enum StatusContestacao {
-    PENDENTE, RESOLVIDA
+    PENDENTE, DEFERIDA, INDEFERIDA
 }
diff --git a/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/Cobranca.java b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/Cobranca.java
index 24f20d8..e614b9b 100644
--- a/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/Cobranca.java
+++ b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/Cobranca.java
@@ -56,14 +56,43 @@ public class Cobranca {
         return new ContestacaoRegistradaEvento(this);
     }
 
-    public ContestacaoResolvidaEvento resolverContestacao(String parecer) {
+    public ContestacaoResolvidaEvento indeferirContestacao(String parecer) {
         if (contestacao == null)
             throw new IllegalStateException("não há contestação registrada");
-        contestacao.resolver(parecer);
+        contestacao.indeferir(parecer);
         this.status = StatusCobranca.ABERTA;
         return new ContestacaoResolvidaEvento(this);
     }
 
+    public ContestacaoResolvidaEvento deferirContestacao(ModoAjuste modo, BigDecimal valor, String parecer) {
+        if (contestacao == null)
+            throw new IllegalStateException("não há contestação registrada");
+        notNull(modo, "modo obrigatório");
+        notNull(valor, "valor obrigatório");
+        var novoValor = calcularValorDeferido(modo, valor);
+        this.historico.add(new HistoricoVersao(this.versao, this.valorAtual, "Contestação deferida", LocalDate.now()));
+        this.versao++;
+        this.valorAtual = novoValor;
+        contestacao.deferir(parecer);
+        this.status = StatusCobranca.ABERTA;
+        return new ContestacaoResolvidaEvento(this);
+    }
+
+    private BigDecimal calcularValorDeferido(ModoAjuste modo, BigDecimal valor) {
+        if (modo == ModoAjuste.PERCENTUAL) {
+            var cinco = new BigDecimal("5");
+            isTrue(valor.compareTo(cinco) >= 0 && valor.compareTo(new BigDecimal("50")) <= 0
+                    && valor.remainder(cinco).compareTo(BigDecimal.ZERO) == 0,
+                    "percentual deve ser múltiplo de 5 entre 5 e 50");
+            var fator = BigDecimal.ONE.subtract(valor.divide(new BigDecimal("100"), MathContext.DECIMAL64));
+            return this.valorAtual.multiply(fator).setScale(2, java.math.RoundingMode.HALF_UP);
+        }
+        var minimo = this.valorAtual.multiply(new BigDecimal("0.5"));
+        isTrue(valor.compareTo(minimo) >= 0 && valor.compareTo(this.valorAtual) < 0,
+                "valor deve reduzir no máximo 50% e ser menor que o valor atual");
+        return valor.setScale(2, java.math.RoundingMode.HALF_UP);
+    }
+
     public DescontoAplicadoEvento aplicarDesconto(BigDecimal percentual, String autorizacaoId, LocalDate data) {
         notNull(percentual, "percentual obrigatório");
         isTrue(percentual.compareTo(BigDecimal.ZERO) > 0 && percentual.compareTo(new BigDecimal("100")) < 0,
diff --git a/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/Contestacao.java b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/Contestacao.java
index d9e23c5..ab21da1 100644
--- a/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/Contestacao.java
+++ b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/Contestacao.java
@@ -22,11 +22,19 @@ public class Contestacao {
         this.status = StatusContestacao.PENDENTE;
     }
 
-    public void resolver(String parecer) {
+    public void deferir(String parecer) {
+        resolverComo(StatusContestacao.DEFERIDA, parecer);
+    }
+
+    public void indeferir(String parecer) {
+        resolverComo(StatusContestacao.INDEFERIDA, parecer);
+    }
+
+    private void resolverComo(StatusContestacao novoStatus, String parecer) {
         notNull(parecer, "parecer obrigatório");
         if (status != StatusContestacao.PENDENTE)
             throw new IllegalStateException("contestação já foi resolvida");
-        this.status = StatusContestacao.RESOLVIDA;
+        this.status = novoStatus;
         this.parecer = parecer;
     }
 
diff --git a/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/CobrancaTest.java b/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/CobrancaTest.java
index c01e52c..2bab8a2 100644
--- a/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/CobrancaTest.java
+++ b/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/CobrancaTest.java
@@ -54,12 +54,77 @@ class CobrancaTest {
     }
 
     @Test
-    void resolverContestacao_deveMudarStatusContestacaoParaResolvida() {
+    void indeferirContestacao_mantemValorEMarcaIndeferida() {
         var cobranca = criarCobranca();
+        var valorAntes = cobranca.getValorAtual();
         cobranca.contestar(new EstudanteId(1), "motivo", LocalDate.now());
-        cobranca.resolverContestacao("Cobrado corretamente");
-        assertEquals(StatusContestacao.RESOLVIDA, cobranca.getContestacao().getStatus());
+        cobranca.indeferirContestacao("Cobrança correta");
+        assertEquals(StatusContestacao.INDEFERIDA, cobranca.getContestacao().getStatus());
         assertEquals(StatusCobranca.ABERTA, cobranca.getStatus());
+        assertEquals(0, valorAntes.compareTo(cobranca.getValorAtual()));
+    }
+
+    @Test
+    void deferirContestacao_percentual_reduzValorEIncrementaVersao() {
+        var cobranca = criarCobranca(); // valorBase 1500.00
+        int versaoAntes = cobranca.getVersao();
+        cobranca.contestar(new EstudanteId(1), "motivo", LocalDate.now());
+        cobranca.deferirContestacao(ModoAjuste.PERCENTUAL, new BigDecimal("20"), "Deferido parcial");
+        assertEquals(StatusContestacao.DEFERIDA, cobranca.getContestacao().getStatus());
+        assertEquals(StatusCobranca.ABERTA, cobranca.getStatus());
+        assertEquals(0, new BigDecimal("1200.00").compareTo(cobranca.getValorAtual()));
+        assertEquals(versaoAntes + 1, cobranca.getVersao());
+    }
+
+    @Test
+    void deferirContestacao_percentualMaximo50() {
+        var cobranca = criarCobranca(); // 1500.00
+        cobranca.contestar(new EstudanteId(1), "motivo", LocalDate.now());
+        cobranca.deferirContestacao(ModoAjuste.PERCENTUAL, new BigDecimal("50"), "Metade");
+        assertEquals(0, new BigDecimal("750.00").compareTo(cobranca.getValorAtual()));
+    }
+
+    @Test
+    void deferirContestacao_percentualInvalido_rejeitado() {
+        var cobranca = criarCobranca();
+        cobranca.contestar(new EstudanteId(1), "motivo", LocalDate.now());
+        assertThrows(IllegalArgumentException.class, () ->
+                cobranca.deferirContestacao(ModoAjuste.PERCENTUAL, new BigDecimal("55"), "x"));
+        var outra = criarCobranca();
+        outra.contestar(new EstudanteId(1), "motivo", LocalDate.now());
+        assertThrows(IllegalArgumentException.class, () ->
+                outra.deferirContestacao(ModoAjuste.PERCENTUAL, new BigDecimal("7"), "x"));
+    }
+
+    @Test
+    void deferirContestacao_valorAbsoluto_defineValor() {
+        var cobranca = criarCobranca(); // 1500.00
+        cobranca.contestar(new EstudanteId(1), "motivo", LocalDate.now());
+        cobranca.deferirContestacao(ModoAjuste.VALOR, new BigDecimal("1000.00"), "Novo valor");
+        assertEquals(0, new BigDecimal("1000.00").compareTo(cobranca.getValorAtual()));
+    }
+
+    @Test
+    void deferirContestacao_valorAbaixoDe50pct_rejeitado() {
+        var cobranca = criarCobranca(); // 1500.00 → mínimo 750.00
+        cobranca.contestar(new EstudanteId(1), "motivo", LocalDate.now());
+        assertThrows(IllegalArgumentException.class, () ->
+                cobranca.deferirContestacao(ModoAjuste.VALOR, new BigDecimal("700.00"), "x"));
+    }
+
+    @Test
+    void resolverContestacaoInexistente_rejeitado() {
+        var cobranca = criarCobranca();
+        assertThrows(IllegalStateException.class, () -> cobranca.indeferirContestacao("x"));
+    }
+
+    @Test
+    void resolverDuasVezes_rejeitado() {
+        var cobranca = criarCobranca();
+        cobranca.contestar(new EstudanteId(1), "motivo", LocalDate.now());
+        cobranca.indeferirContestacao("primeira");
+        assertThrows(IllegalStateException.class, () -> cobranca.deferirContestacao(
+                ModoAjuste.PERCENTUAL, new BigDecimal("10"), "segunda"));
     }
 
     @Test
diff --git a/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/ResolverContestacaoFuncionalidade.java b/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/ResolverContestacaoFuncionalidade.java
index 4163200..02b1b70 100644
--- a/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/ResolverContestacaoFuncionalidade.java
+++ b/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/ResolverContestacaoFuncionalidade.java
@@ -41,41 +41,58 @@ public class ResolverContestacaoFuncionalidade {
                 new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
         cobrancaId = cobranca.getId();
         ctx.servico.contestar(cobrancaId, new EstudanteId(800 + contratoId), "Contestação inicial");
-        ctx.servico.resolverContestacao(cobrancaId, "Primeira resolução");
+        ctx.servico.indeferirContestacao(cobrancaId, "Primeira resolução");
     }
 
-    @Quando("o setor financeiro resolve a contestação com parecer {string}")
-    public void setorFinanceiroResolveContestacao(String parecer) {
-        ctx.servico.resolverContestacao(cobrancaId, parecer);
+    @Quando("o setor financeiro indefere a contestação com parecer {string}")
+    public void indefere(String parecer) {
+        ctx.servico.indeferirContestacao(cobrancaId, parecer);
     }
 
-    @Quando("o setor financeiro tenta resolver a contestação")
-    public void setorFinanceiroTentaResolverContestacao() {
+    @Quando("o setor financeiro defere a contestação com {int} por cento e parecer {string}")
+    public void deferePercentual(int pct, String parecer) {
+        ctx.servico.deferirContestacao(cobrancaId, ModoAjuste.PERCENTUAL, new BigDecimal(pct), parecer);
+    }
+
+    @Quando("o setor financeiro defere a contestação com o valor {string} e parecer {string}")
+    public void defereValor(String valor, String parecer) {
+        ctx.servico.deferirContestacao(cobrancaId, ModoAjuste.VALOR,
+                new BigDecimal(valor).setScale(2, java.math.RoundingMode.HALF_UP), parecer);
+    }
+
+    @Quando("o setor financeiro tenta indeferir a contestação")
+    public void tentaIndeferir() {
         try {
-            ctx.servico.resolverContestacao(cobrancaId, "parecer");
+            ctx.servico.indeferirContestacao(cobrancaId, "parecer");
         } catch (RuntimeException e) {
             ctx.excecao = e;
         }
     }
 
-    @Quando("o setor financeiro tenta resolver a contestação novamente")
-    public void setorFinanceiroTentaResolverNovamente() {
+    @Quando("o setor financeiro tenta indeferir a contestação novamente")
+    public void tentaIndeferirNovamente() {
         try {
-            ctx.servico.resolverContestacao(cobrancaId, "novo parecer");
+            ctx.servico.indeferirContestacao(cobrancaId, "novo parecer");
         } catch (RuntimeException e) {
             ctx.excecao = e;
         }
     }
 
-    @Entao("a contestação deve ter status RESOLVIDA")
-    public void contestacaoDeveTerStatusResolvida() {
+    @Entao("a contestação deve ter status {string}")
+    public void contestacaoDeveTerStatus(String status) {
         var contestacao = ctx.repositorio.obter(cobrancaId).getContestacao();
         Assertions.assertNotNull(contestacao);
-        Assertions.assertEquals(StatusContestacao.RESOLVIDA, contestacao.getStatus());
+        Assertions.assertEquals(StatusContestacao.valueOf(status), contestacao.getStatus());
     }
 
     @E("a cobrança deve retornar ao status ABERTA após resolução")
     public void cobrancaDeveRetornarAoStatusAbertaAposResolucao() {
         Assertions.assertEquals(StatusCobranca.ABERTA, ctx.repositorio.obter(cobrancaId).getStatus());
     }
+
+    @E("o valor atual da cobrança permanece {string}")
+    public void valorAtualPermanece(String valor) {
+        Assertions.assertEquals(0, new BigDecimal(valor).setScale(2, java.math.RoundingMode.HALF_UP)
+                .compareTo(ctx.repositorio.obter(cobrancaId).getValorAtual()));
+    }
 }
diff --git a/dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/resolver_contestacao.feature b/dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/resolver_contestacao.feature
index 51b08fe..7322161 100644
--- a/dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/resolver_contestacao.feature
+++ b/dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/resolver_contestacao.feature
@@ -2,18 +2,31 @@
 
 Funcionalidade: Resolver Contestação de Cobrança
 
-  Cenário: Resolver contestação pendente com parecer
+  Cenário: Indeferir contestação mantém o valor
     Dado uma cobrança contestada pelo estudante 1 no contrato 50
-    Quando o setor financeiro resolve a contestação com parecer "Valor conferido e correto"
-    Então a contestação deve ter status RESOLVIDA
+    Quando o setor financeiro indefere a contestação com parecer "Cobrança correta"
+    Então a contestação deve ter status "INDEFERIDA"
     E a cobrança deve retornar ao status ABERTA após resolução
+    E o valor atual da cobrança permanece "1500.00"
+
+  Cenário: Deferir com percentual reduz o valor
+    Dado uma cobrança contestada pelo estudante 1 no contrato 50
+    Quando o setor financeiro defere a contestação com 20 por cento e parecer "Ajuste deferido"
+    Então a contestação deve ter status "DEFERIDA"
+    E o valor atual da cobrança permanece "1200.00"
+
+  Cenário: Deferir com valor absoluto define o valor
+    Dado uma cobrança contestada pelo estudante 1 no contrato 50
+    Quando o setor financeiro defere a contestação com o valor "1000.00" e parecer "Novo valor"
+    Então a contestação deve ter status "DEFERIDA"
+    E o valor atual da cobrança permanece "1000.00"
 
   Cenário: Rejeitar resolução de cobrança sem contestação
     Dado uma cobrança aberta sem contestação para o contrato 51
-    Quando o setor financeiro tenta resolver a contestação
+    Quando o setor financeiro tenta indeferir a contestação
     Então o sistema deve rejeitar informando "não há contestação registrada"
 
   Cenário: Rejeitar segunda resolução de contestação já resolvida
     Dado uma cobrança com contestação já resolvida para o contrato 52
-    Quando o setor financeiro tenta resolver a contestação novamente
+    Quando o setor financeiro tenta indeferir a contestação novamente
     Então o sistema deve rejeitar informando "contestação já foi resolvida"

============================================================
## UNIT 2 — INFRA + CONTROLLER (Tasks 3-4)
============================================================
# U2 Review Package — Contestação: infra + controller (Tasks 3-4)

No commits. Modified files diffed vs PRE-feature baseline (only this feature's changes show). New V6 vs /dev/null.

## === MODIFIED: CobrancaJpa.java (vs pre-feature baseline) ===
diff --git a/.superpowers/sdd/baseline/contestacao/CobrancaJpa.java.base b/infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/CobrancaJpa.java
index 4757b17..cf96695 100644
--- a/.superpowers/sdd/baseline/contestacao/CobrancaJpa.java.base
+++ b/infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/CobrancaJpa.java
@@ -220,8 +220,11 @@ class CobrancaRepositorioImpl implements CobrancaRepositorio, CobrancaRepositori
         if (jpa.contestacaoRequerente != null) {
             contestacao = new Contestacao(new EstudanteId(jpa.contestacaoRequerente),
                     jpa.contestacaoJustificativa, jpa.contestacaoData);
-            if (jpa.contestacaoStatus == StatusContestacao.RESOLVIDA)
-                contestacao.resolver(jpa.contestacaoParecer != null ? jpa.contestacaoParecer : "");
+            var parecer = jpa.contestacaoParecer != null ? jpa.contestacaoParecer : "";
+            if (jpa.contestacaoStatus == StatusContestacao.DEFERIDA)
+                contestacao.deferir(parecer);
+            else if (jpa.contestacaoStatus == StatusContestacao.INDEFERIDA)
+                contestacao.indeferir(parecer);
         }
 
         var historico = jpa.historico.stream()

## === MODIFIED: CobrancaControlador.java (vs pre-feature baseline) ===
diff --git a/.superpowers/sdd/baseline/contestacao/CobrancaControlador.java.base b/apresentacao-backend/src/main/java/school/cesar/acadlab/apresentacao/gestaofinanceira/CobrancaControlador.java
index b3394ca..6477a58 100644
--- a/.superpowers/sdd/baseline/contestacao/CobrancaControlador.java.base
+++ b/apresentacao-backend/src/main/java/school/cesar/acadlab/apresentacao/gestaofinanceira/CobrancaControlador.java
@@ -21,6 +21,7 @@ import school.cesar.acadlab.dominio.gestaofinanceira.CobrancaServico;
 import school.cesar.acadlab.dominio.gestaofinanceira.ContratoId;
 import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
 import school.cesar.acadlab.dominio.gestaofinanceira.PeriodoLetivoId;
+import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.ModoAjuste;
 import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.Pagamento;
 
 @RestController
@@ -67,9 +68,15 @@ class CobrancaControlador {
         servico.contestar(new CobrancaId(id), new EstudanteId(request.estudanteId()), request.justificativa());
     }
 
-    @RequestMapping(method = POST, path = "{id}/resolver-contestacao")
-    void resolverContestacao(@PathVariable int id, @RequestBody String parecer) {
-        servico.resolverContestacao(new CobrancaId(id), parecer);
+    @RequestMapping(method = POST, path = "{id}/deferir-contestacao")
+    void deferirContestacao(@PathVariable int id, @RequestBody DeferirContestacaoRequest request) {
+        servico.deferirContestacao(new CobrancaId(id), ModoAjuste.valueOf(request.modo()),
+                request.valor(), request.parecer());
+    }
+
+    @RequestMapping(method = POST, path = "{id}/indeferir-contestacao")
+    void indeferirContestacao(@PathVariable int id, @RequestBody IndeferirContestacaoRequest request) {
+        servico.indeferirContestacao(new CobrancaId(id), request.parecer());
     }
 
     @RequestMapping(method = POST, path = "{id}/registrar-pagamento")
@@ -99,4 +106,8 @@ class CobrancaControlador {
     record RegistrarPagamentoRequest(BigDecimal valor, LocalDate data, String referencia) {}
 
     record CancelarPagamentoRequest(String justificativa, String responsavel) {}
+
+    record DeferirContestacaoRequest(String modo, BigDecimal valor, String parecer) {}
+
+    record IndeferirContestacaoRequest(String parecer) {}
 }

## === NEW: V6__contestacao_seed.sql (vs /dev/null) ===
diff --git a/apresentacao-backend/src/main/resources/db/migration/V6__contestacao_seed.sql b/apresentacao-backend/src/main/resources/db/migration/V6__contestacao_seed.sql
new file mode 100644
index 0000000..84707f5
--- /dev/null
+++ b/apresentacao-backend/src/main/resources/db/migration/V6__contestacao_seed.sql
@@ -0,0 +1,8 @@
+-- ─── CONTESTAÇÃO seed (cobrança 6, que já é CONTESTADA no V1) ─────────────────
+-- V1 já aplicado não pode ser editado; populamos as colunas de contestação aqui.
+UPDATE cobranca
+   SET contestacao_requerente   = 3,
+       contestacao_justificativa = 'Valor cobrado diverge do contrato firmado.',
+       contestacao_data         = '2025-08-15',
+       contestacao_status       = 'PENDENTE'
+ WHERE id = 6 AND contestacao_requerente IS NULL;

============================================================
## UNIT 3 — FRONTEND (Tasks 5-6)
============================================================
# U3 Review Package — Contestação: frontend (Tasks 5-6)

No commits. Each file diffed vs its PRE-feature baseline → only THIS feature's changes show (Sub-0/Sub-A changes are in the baseline and excluded).

## === financeiro.ts (type + hooks) ===
diff --git a/.superpowers/sdd/baseline/contestacao/financeiro.ts.base b/apresentacao-frontend/react/src/lib/financeiro.ts
index e727705..66d00f5 100644
--- a/.superpowers/sdd/baseline/contestacao/financeiro.ts.base
+++ b/apresentacao-frontend/react/src/lib/financeiro.ts
@@ -17,7 +17,7 @@ export type ContestacaoResumo = {
   requerenteId: number | null;
   justificativa: string | null;
   data: string | null;
-  status: "PENDENTE" | "RESOLVIDA" | null;
+  status: "PENDENTE" | "DEFERIDA" | "INDEFERIDA" | null;
   parecer: string | null;
 };
 
@@ -97,11 +97,26 @@ export function useContestar() {
   });
 }
 
-export function useResolverContestacao() {
+export type ModoAjuste = "PERCENTUAL" | "VALOR";
+
+export function useDeferirContestacao() {
+  const invalidate = useInvalidate();
+  return useMutation({
+    mutationFn: (vars: { id: number; modo: ModoAjuste; valor: number; parecer: string }) =>
+      api.post(`cobrancas/${vars.id}/deferir-contestacao`, {
+        modo: vars.modo,
+        valor: vars.valor,
+        parecer: vars.parecer,
+      }),
+    onSuccess: invalidate,
+  });
+}
+
+export function useIndeferirContestacao() {
   const invalidate = useInvalidate();
   return useMutation({
     mutationFn: (vars: { id: number; parecer: string }) =>
-      api.post(`cobrancas/${vars.id}/resolver-contestacao`, vars.parecer),
+      api.post(`cobrancas/${vars.id}/indeferir-contestacao`, { parecer: vars.parecer }),
     onSuccess: invalidate,
   });
 }

## === financeiro.tsx (Contestações tab + resolution panel — MUST be tab-scoped only) ===
diff --git a/.superpowers/sdd/baseline/contestacao/financeiro.tsx.base b/apresentacao-frontend/react/src/routes/financeiro.tsx
index 2519900..2653c39 100644
--- a/.superpowers/sdd/baseline/contestacao/financeiro.tsx.base
+++ b/apresentacao-frontend/react/src/routes/financeiro.tsx
@@ -12,7 +12,7 @@ import { ArrowLeft, Download, CreditCard, AlertCircle, Send, FileText, Plus } fr
 import { toast } from "sonner";
 import {
   useExtrato, useRegistrarPagamento, useContestar,
-  useContestacoesAbertas, useResolverContestacao,
+  useContestacoesAbertas, useDeferirContestacao, useIndeferirContestacao, type ModoAjuste,
   useBolsas, useConcederBolsa, useSuspenderBolsa, useReativarBolsa, useRenovarBolsa,
   type CobrancaResumo, type StatusCobranca, type BolsaResumo, type TipoBolsa, type StatusBolsa,
 } from "@/lib/financeiro";
@@ -209,7 +209,17 @@ function FinanceiroView() {
 
   const contestacoesQuery = useContestacoesAbertas();
   const contestacoesAbertas = contestacoesQuery.data ?? [];
-  const resolver = useResolverContestacao();
+  const deferir = useDeferirContestacao();
+  const indeferir = useIndeferirContestacao();
+  const [resolverId, setResolverId] = useState<number | null>(null);
+  const [decisao, setDecisao] = useState<"DEFERIR" | "INDEFERIR">("DEFERIR");
+  const [modoAjuste, setModoAjuste] = useState<ModoAjuste>("PERCENTUAL");
+  const [valorAjuste, setValorAjuste] = useState("");
+  const [parecerResol, setParecerResol] = useState("");
+  const cobrancaResolver = contestacoesAbertas.find((c) => c.id === resolverId) ?? null;
+  const fecharResolver = () => { setResolverId(null); setValorAjuste(""); setParecerResol(""); setDecisao("DEFERIR"); setModoAjuste("PERCENTUAL"); };
+  const rotuloStatusContestacao: Record<string, string> = { PENDENTE: "Em análise", DEFERIDA: "Deferida", INDEFERIDA: "Indeferida" };
+  const tomStatusContestacao = (s: string | null | undefined) => (s === "DEFERIDA" ? "success" : s === "INDEFERIDA" ? "danger" : "info");
 
   const [inadimp, setInadimp] = useState<Inadimplente[]>([
     { matricula: "2021.0451", aluno: "Lucas Pereira", curso: "Eng. Civil", emAtraso: "R$ 2.840,00", diasAtraso: 47, status: "Notificado" },
@@ -291,16 +301,77 @@ function FinanceiroView() {
               { key: "cobranca", header: "Cobrança", render: (r) => `Mensalidade • venc. ${formatData(r.vencimento)}` },
               { key: "valor", header: "Valor", align: "right", render: (r) => formatMoeda(r.valorAtual) },
               { key: "motivo", header: "Motivo", render: (r) => r.contestacao?.justificativa ?? "—" },
-              { key: "status", header: "Status", render: () => <StatusBadge tone="info">Em análise</StatusBadge> },
+              { key: "status", header: "Status", render: (r) => (
+                <StatusBadge tone={tomStatusContestacao(r.contestacao?.status)}>
+                  {rotuloStatusContestacao[r.contestacao?.status ?? "PENDENTE"] ?? "Em análise"}
+                </StatusBadge>
+              )},
               { key: "acoes", header: "", align: "right", render: (r) => (
                 <div className="flex justify-end gap-2">
-                  <RowActionButton onClick={() => resolver.mutate({ id: r.id, parecer: "Indeferida pelo Setor Financeiro." }, { onSuccess: () => toast.success(`Contestação COB-${r.id} indeferida.`) })}>Indeferir</RowActionButton>
-                  <RowActionButton tone="info" onClick={() => resolver.mutate({ id: r.id, parecer: "Deferida pelo Setor Financeiro." }, { onSuccess: () => toast.success(`Contestação COB-${r.id} deferida.`) })}>Deferir</RowActionButton>
+                  <RowActionButton tone="info" onClick={() => { setResolverId(r.id); }}>Resolver</RowActionButton>
                 </div>
               )},
             ]}
             rows={contestacoesAbertas}
           />
+          {cobrancaResolver && (
+            <div className="rounded-xl border bg-card p-5 shadow-card">
+              <SectionTitle title={`Resolver contestação COB-${cobrancaResolver.id}`}
+                subtitle={`Valor atual ${formatMoeda(cobrancaResolver.valorAtual)} • Estudante #${cobrancaResolver.estudanteId}`} />
+              <div className="mt-3 flex flex-col gap-3">
+                <div className="flex gap-2">
+                  <Button variant={decisao === "DEFERIR" ? "default" : "outline"} onClick={() => setDecisao("DEFERIR")}>Deferir</Button>
+                  <Button variant={decisao === "INDEFERIR" ? "default" : "outline"} onClick={() => setDecisao("INDEFERIR")}>Indeferir</Button>
+                </div>
+                {decisao === "DEFERIR" && (
+                  <div className="grid grid-cols-2 gap-3">
+                    <FormField label="Modo">
+                      <select className="h-9 w-full rounded-md border bg-background px-3 text-sm"
+                        value={modoAjuste} onChange={(e) => setModoAjuste(e.target.value as ModoAjuste)}>
+                        <option value="PERCENTUAL">Percentual (%)</option>
+                        <option value="VALOR">Valor (R$)</option>
+                      </select>
+                    </FormField>
+                    <FormField label={modoAjuste === "PERCENTUAL" ? "Desconto (%)" : "Novo valor (R$)"} required>
+                      {modoAjuste === "PERCENTUAL" ? (
+                        <select className="h-9 w-full rounded-md border bg-background px-3 text-sm"
+                          value={valorAjuste} onChange={(e) => setValorAjuste(e.target.value)}>
+                          <option value="">Selecione…</option>
+                          {[5, 10, 15, 20, 25, 30, 35, 40, 45, 50].map((p) => (
+                            <option key={p} value={p}>{p}%</option>
+                          ))}
+                        </select>
+                      ) : (
+                        <Input type="number" placeholder={`entre ${formatMoeda(cobrancaResolver.valorAtual * 0.5)} e ${formatMoeda(cobrancaResolver.valorAtual)}`}
+                          value={valorAjuste} onChange={(e) => setValorAjuste(e.target.value)} />
+                      )}
+                    </FormField>
+                  </div>
+                )}
+                <FormField label="Parecer" required>
+                  <Input placeholder="Justificativa da decisão" value={parecerResol} onChange={(e) => setParecerResol(e.target.value)} />
+                </FormField>
+              </div>
+              <div className="mt-4 flex justify-end gap-2">
+                <Button variant="outline" onClick={fecharResolver}>Cancelar</Button>
+                <Button onClick={() => {
+                  if (!parecerResol.trim()) { toast.error("Informe o parecer."); return; }
+                  if (decisao === "INDEFERIR") {
+                    indeferir.mutate({ id: cobrancaResolver.id, parecer: parecerResol },
+                      { onSuccess: () => { fecharResolver(); toast.success(`Contestação COB-${cobrancaResolver.id} indeferida.`); } });
+                    return;
+                  }
+                  const v = Number(valorAjuste);
+                  if (!valorAjuste || Number.isNaN(v) || v <= 0) { toast.error("Informe o valor do deferimento."); return; }
+                  if (modoAjuste === "VALOR" && (v >= cobrancaResolver.valorAtual || v < cobrancaResolver.valorAtual * 0.5)) {
+                    toast.error("Valor deve reduzir no máximo 50% e ser menor que o atual."); return;
+                  }
+                  deferir.mutate({ id: cobrancaResolver.id, modo: modoAjuste, valor: v, parecer: parecerResol },
+                    { onSuccess: () => { fecharResolver(); toast.success(`Contestação COB-${cobrancaResolver.id} deferida.`); } });
+                }}>Confirmar</Button>
+              </div>
+            </div>
+          )}
         </>
       )}
 
