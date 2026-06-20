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
