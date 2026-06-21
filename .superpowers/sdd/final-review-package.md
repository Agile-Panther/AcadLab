# Final Whole-Feature Review Package — F-13 Sub-A (Bolsas & Descontos)

Union of all 3 units' diffs (no commits — working tree vs pre-Sub-A baselines / /dev/null). Sub-0 and F-09 changes are excluded (in baselines).

============================================================
## UNIT 1 — DOMAIN (Tasks 1-3)
============================================================
# U1 Review Package — F-13 Sub-A domain (Tasks 1-3)

All files are NEW (untracked). No commits (per user instruction). Full content shown as diff against /dev/null.

## File list
  dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/AutorizacaoDescontoPorBolsa.java
  dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/Bolsa.java
  dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaId.java
  dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaRepositorio.java
  dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaServico.java
  dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/StatusBolsa.java
  dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/TipoBolsa.java
  dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/AplicarDescontoComBolsaSteps.java
  dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaFuncionalidade.java
  dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaRepositorioFake.java
  dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaTest.java
  dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/CicloVidaBolsaSteps.java
  dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/aplicar_desconto_com_bolsa.feature
  dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/conceder_bolsa.feature
  dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/renovar_bolsa.feature
  dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/suspender_reativar_bolsa.feature

## Full diff
diff --git a/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/AutorizacaoDescontoPorBolsa.java b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/AutorizacaoDescontoPorBolsa.java
new file mode 100644
index 0000000..c633401
--- /dev/null
+++ b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/AutorizacaoDescontoPorBolsa.java
@@ -0,0 +1,24 @@
+package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;
+
+import school.cesar.acadlab.dominio.gestaofinanceira.VerificadorAutorizacaoDesconto;
+
+public class AutorizacaoDescontoPorBolsa implements VerificadorAutorizacaoDesconto {
+    private final BolsaRepositorio repositorio;
+
+    public AutorizacaoDescontoPorBolsa(BolsaRepositorio repositorio) {
+        this.repositorio = repositorio;
+    }
+
+    @Override
+    public boolean autorizacaoValida(String autorizacaoId) {
+        if (autorizacaoId == null) return false;
+        final int id;
+        try {
+            id = Integer.parseInt(autorizacaoId.trim());
+        } catch (NumberFormatException e) {
+            return false;
+        }
+        return repositorio.listar().stream()
+                .anyMatch(b -> b.getId().valor() == id && b.getStatus() == StatusBolsa.ATIVA);
+    }
+}
diff --git a/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/Bolsa.java b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/Bolsa.java
new file mode 100644
index 0000000..8695347
--- /dev/null
+++ b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/Bolsa.java
@@ -0,0 +1,110 @@
+package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;
+
+import static org.apache.commons.lang3.Validate.isTrue;
+import static org.apache.commons.lang3.Validate.notNull;
+
+import java.math.BigDecimal;
+import java.time.LocalDate;
+
+import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
+
+public class Bolsa {
+    private static final BigDecimal CEM = new BigDecimal("100");
+
+    private final BolsaId id;
+    private final EstudanteId estudanteId;
+    private final TipoBolsa tipo;
+    private final BigDecimal percentual;
+    private LocalDate validade;
+    private StatusBolsa status;
+
+    private Bolsa(BolsaId id, EstudanteId estudanteId, TipoBolsa tipo, BigDecimal percentual,
+            LocalDate validade, StatusBolsa status) {
+        notNull(id, "id obrigatório");
+        notNull(estudanteId, "estudanteId obrigatório");
+        notNull(tipo, "tipo obrigatório");
+        notNull(percentual, "percentual obrigatório");
+        isTrue(percentual.compareTo(BigDecimal.ZERO) >= 0 && percentual.compareTo(CEM) <= 0,
+                "percentual deve estar entre 0 e 100");
+        if (validade == null) throw new IllegalArgumentException("validade obrigatória");
+        notNull(status, "status obrigatório");
+        this.id = id;
+        this.estudanteId = estudanteId;
+        this.tipo = tipo;
+        this.percentual = percentual;
+        this.validade = validade;
+        this.status = status;
+    }
+
+    public static Bolsa conceder(BolsaId id, EstudanteId estudanteId, TipoBolsa tipo,
+            BigDecimal percentual, LocalDate validade) {
+        return new Bolsa(id, estudanteId, tipo, percentual, validade, StatusBolsa.ATIVA);
+    }
+
+    public static Bolsa reconstituir(BolsaId id, EstudanteId estudanteId, TipoBolsa tipo,
+            BigDecimal percentual, LocalDate validade, StatusBolsa status) {
+        return new Bolsa(id, estudanteId, tipo, percentual, validade, status);
+    }
+
+    public BolsaConcedidaEvento eventoConcessao() { return new BolsaConcedidaEvento(this); }
+
+    public BolsaSuspensaEvento suspender() {
+        if (status == StatusBolsa.SUSPENSA)
+            throw new IllegalStateException("bolsa já está suspensa");
+        this.status = StatusBolsa.SUSPENSA;
+        return new BolsaSuspensaEvento(this);
+    }
+
+    public BolsaReativadaEvento reativar() {
+        if (status != StatusBolsa.SUSPENSA)
+            throw new IllegalStateException("só é possível reativar uma bolsa suspensa");
+        this.status = StatusBolsa.ATIVA;
+        return new BolsaReativadaEvento(this);
+    }
+
+    public RenovacaoSolicitadaEvento solicitarRenovacao() {
+        if (status != StatusBolsa.ATIVA)
+            throw new IllegalStateException("só é possível solicitar renovação de uma bolsa ativa");
+        this.status = StatusBolsa.EM_RENOVACAO;
+        return new RenovacaoSolicitadaEvento(this);
+    }
+
+    public BolsaRenovadaEvento renovar(LocalDate novaValidade) {
+        notNull(novaValidade, "novaValidade obrigatória");
+        if (status == StatusBolsa.SUSPENSA)
+            throw new IllegalStateException("não é possível renovar uma bolsa suspensa");
+        isTrue(novaValidade.isAfter(this.validade), "novaValidade deve ser posterior à validade atual");
+        this.validade = novaValidade;
+        this.status = StatusBolsa.ATIVA;
+        return new BolsaRenovadaEvento(this);
+    }
+
+    public BolsaId getId() { return id; }
+    public EstudanteId getEstudanteId() { return estudanteId; }
+    public TipoBolsa getTipo() { return tipo; }
+    public BigDecimal getPercentual() { return percentual; }
+    public LocalDate getValidade() { return validade; }
+    public StatusBolsa getStatus() { return status; }
+
+    /* ===== Eventos (Observer) ===== */
+    public abstract static class BolsaEvento {
+        private final Bolsa bolsa;
+        protected BolsaEvento(Bolsa bolsa) { this.bolsa = bolsa; }
+        public Bolsa getBolsa() { return bolsa; }
+    }
+    public static class BolsaConcedidaEvento extends BolsaEvento {
+        private BolsaConcedidaEvento(Bolsa b) { super(b); }
+    }
+    public static class BolsaSuspensaEvento extends BolsaEvento {
+        private BolsaSuspensaEvento(Bolsa b) { super(b); }
+    }
+    public static class BolsaReativadaEvento extends BolsaEvento {
+        private BolsaReativadaEvento(Bolsa b) { super(b); }
+    }
+    public static class RenovacaoSolicitadaEvento extends BolsaEvento {
+        private RenovacaoSolicitadaEvento(Bolsa b) { super(b); }
+    }
+    public static class BolsaRenovadaEvento extends BolsaEvento {
+        private BolsaRenovadaEvento(Bolsa b) { super(b); }
+    }
+}
diff --git a/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaId.java b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaId.java
new file mode 100644
index 0000000..18dc5f3
--- /dev/null
+++ b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaId.java
@@ -0,0 +1,3 @@
+package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;
+
+public record BolsaId(int valor) {}
diff --git a/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaRepositorio.java b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaRepositorio.java
new file mode 100644
index 0000000..49b5f06
--- /dev/null
+++ b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaRepositorio.java
@@ -0,0 +1,10 @@
+package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;
+
+import java.util.List;
+
+public interface BolsaRepositorio {
+    BolsaId proximoId();
+    void salvar(Bolsa bolsa);
+    Bolsa obter(BolsaId id);
+    List<Bolsa> listar();
+}
diff --git a/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaServico.java b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaServico.java
new file mode 100644
index 0000000..e9f4dc4
--- /dev/null
+++ b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaServico.java
@@ -0,0 +1,56 @@
+package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;
+
+import static org.apache.commons.lang3.Validate.notNull;
+
+import java.math.BigDecimal;
+import java.time.LocalDate;
+
+import school.cesar.acadlab.dominio.evento.EventoBarramento;
+import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
+
+public class BolsaServico {
+    private final BolsaRepositorio repositorio;
+    private final EventoBarramento barramento;
+
+    public BolsaServico(BolsaRepositorio repositorio, EventoBarramento barramento) {
+        notNull(repositorio, "repositório obrigatório");
+        notNull(barramento, "barramento obrigatório");
+        this.repositorio = repositorio;
+        this.barramento = barramento;
+    }
+
+    public Bolsa conceder(EstudanteId estudanteId, TipoBolsa tipo, BigDecimal percentual, LocalDate validade) {
+        var bolsa = Bolsa.conceder(repositorio.proximoId(), estudanteId, tipo, percentual, validade);
+        repositorio.salvar(bolsa);
+        barramento.postar(bolsa.eventoConcessao());
+        return bolsa;
+    }
+
+    public void suspender(BolsaId id) {
+        var bolsa = repositorio.obter(id);
+        var evento = bolsa.suspender();
+        repositorio.salvar(bolsa);
+        barramento.postar(evento);
+    }
+
+    public void reativar(BolsaId id) {
+        var bolsa = repositorio.obter(id);
+        var evento = bolsa.reativar();
+        repositorio.salvar(bolsa);
+        barramento.postar(evento);
+    }
+
+    public void solicitarRenovacao(BolsaId id) {
+        var bolsa = repositorio.obter(id);
+        var evento = bolsa.solicitarRenovacao();
+        repositorio.salvar(bolsa);
+        barramento.postar(evento);
+    }
+
+    public void renovar(BolsaId id, LocalDate novaValidade) {
+        var bolsa = repositorio.obter(id);
+        var evento = bolsa.renovar(novaValidade);
+        repositorio.salvar(bolsa);
+        barramento.postar(evento);
+    }
+}
diff --git a/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/StatusBolsa.java b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/StatusBolsa.java
new file mode 100644
index 0000000..5557a94
--- /dev/null
+++ b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/StatusBolsa.java
@@ -0,0 +1,3 @@
+package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;
+
+public enum StatusBolsa { ATIVA, SUSPENSA, EM_RENOVACAO }
diff --git a/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/TipoBolsa.java b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/TipoBolsa.java
new file mode 100644
index 0000000..e87e839
--- /dev/null
+++ b/dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/TipoBolsa.java
@@ -0,0 +1,3 @@
+package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;
+
+public enum TipoBolsa { PROUNI, FIES, MERITO, CONVENIO }
diff --git a/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/AplicarDescontoComBolsaSteps.java b/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/AplicarDescontoComBolsaSteps.java
new file mode 100644
index 0000000..eda4254
--- /dev/null
+++ b/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/AplicarDescontoComBolsaSteps.java
@@ -0,0 +1,80 @@
+package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;
+
+import io.cucumber.java.pt.Dado;
+import io.cucumber.java.pt.Quando;
+import io.cucumber.java.pt.Entao;
+import io.cucumber.java.pt.E;
+import org.junit.jupiter.api.Assertions;
+import school.cesar.acadlab.dominio.evento.EventoBarramento;
+import school.cesar.acadlab.dominio.evento.EventoObservador;
+import school.cesar.acadlab.dominio.gestaofinanceira.*;
+import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.*;
+import java.math.BigDecimal;
+import java.math.RoundingMode;
+import java.time.LocalDate;
+
+public class AplicarDescontoComBolsaSteps {
+    private final BolsaFuncionalidade bolsaCtx;
+    private CobrancaServico cobrancaServico;
+    private GestaoFinanceiraRepositorioTest cobrancaRepo;
+    private CobrancaId cobrancaId;
+    private String autorizacaoBolsa;
+    private RuntimeException excecao;
+
+    public AplicarDescontoComBolsaSteps(BolsaFuncionalidade bolsaCtx) {
+        this.bolsaCtx = bolsaCtx;
+    }
+
+    private void inicializarCobrancaServico() {
+        cobrancaRepo = new GestaoFinanceiraRepositorioTest();
+        VerificadorMatriculaConfirmada matricula = (e, p) -> true;
+        VerificadorAutorizacaoDesconto autorizacao = new AutorizacaoDescontoPorBolsa(bolsaCtx.repositorio);
+        EventoBarramento barramento = new EventoBarramento() {
+            @Override public <E> void adicionar(EventoObservador<E> o) {}
+            @Override public <E> void postar(E ev) {}
+        };
+        cobrancaServico = new CobrancaServico(cobrancaRepo, matricula, autorizacao, barramento);
+    }
+
+    @Dado("uma bolsa {word} ativa de {int} por cento para o estudante {int}")
+    public void bolsaAtiva(String tipo, int pct, int estudante) {
+        var b = bolsaCtx.servico.conceder(new EstudanteId(estudante), TipoBolsa.valueOf(tipo),
+                new BigDecimal(pct), LocalDate.of(2025, 12, 31));
+        autorizacaoBolsa = String.valueOf(b.getId().valor());
+    }
+
+    @E("uma cobrança aberta de {double} para o estudante {int} contra o contrato {int}")
+    public void cobrancaAberta(double valor, int estudante, int contrato) {
+        if (cobrancaServico == null) inicializarCobrancaServico();
+        var c = cobrancaServico.gerarCobranca(new ContratoId(contrato), new EstudanteId(estudante),
+                new PeriodoLetivoId(1), BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP),
+                LocalDate.of(2025, 2, 10));
+        cobrancaId = c.getId();
+    }
+
+    @Quando("aplico o desconto da bolsa ativa na cobrança")
+    public void aplicoDescontoBolsa() {
+        cobrancaServico.aplicarDesconto(cobrancaId, new BigDecimal("10"), autorizacaoBolsa);
+    }
+
+    @Quando("tento aplicar um desconto de {int} por cento com a autorização {string}")
+    public void tentoAplicar(int pct, String autorizacaoId) {
+        try {
+            cobrancaServico.aplicarDesconto(cobrancaId, new BigDecimal(pct), autorizacaoId);
+        } catch (RuntimeException e) {
+            excecao = e;
+        }
+    }
+
+    @Entao("o valor atual da cobrança deve ser {double} reais")
+    public void valorAtual(double valor) {
+        Assertions.assertEquals(0, BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP)
+                .compareTo(cobrancaRepo.obter(cobrancaId).getValorAtual()));
+    }
+
+    @Entao("o desconto é recusado por autorização inválida")
+    public void recusado() {
+        Assertions.assertNotNull(excecao);
+        Assertions.assertTrue(excecao.getMessage().contains("autorização inválida"));
+    }
+}
diff --git a/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaFuncionalidade.java b/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaFuncionalidade.java
new file mode 100644
index 0000000..8c9db48
--- /dev/null
+++ b/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaFuncionalidade.java
@@ -0,0 +1,17 @@
+package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;
+
+import school.cesar.acadlab.dominio.evento.EventoBarramento;
+import school.cesar.acadlab.dominio.evento.EventoObservador;
+
+public class BolsaFuncionalidade {
+    public final BolsaRepositorioFake repositorio = new BolsaRepositorioFake();
+    public final EventoBarramento barramento = new BarramentoStub();
+    public final BolsaServico servico = new BolsaServico(repositorio, barramento);
+    public BolsaId ultimaBolsa;
+    public RuntimeException excecao;
+
+    static class BarramentoStub implements EventoBarramento {
+        @Override public <E> void adicionar(EventoObservador<E> o) {}
+        @Override public <E> void postar(E e) {}
+    }
+}
diff --git a/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaRepositorioFake.java b/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaRepositorioFake.java
new file mode 100644
index 0000000..7e8ed5b
--- /dev/null
+++ b/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaRepositorioFake.java
@@ -0,0 +1,13 @@
+package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;
+
+import java.util.*;
+
+public class BolsaRepositorioFake implements BolsaRepositorio {
+    private int seq = 1;
+    private final Map<BolsaId, Bolsa> store = new HashMap<>();
+
+    @Override public BolsaId proximoId() { return new BolsaId(seq++); }
+    @Override public void salvar(Bolsa bolsa) { store.put(bolsa.getId(), bolsa); }
+    @Override public Bolsa obter(BolsaId id) { return Optional.ofNullable(store.get(id)).orElseThrow(); }
+    @Override public java.util.List<Bolsa> listar() { return new ArrayList<>(store.values()); }
+}
diff --git a/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaTest.java b/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaTest.java
new file mode 100644
index 0000000..5cac093
--- /dev/null
+++ b/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaTest.java
@@ -0,0 +1,77 @@
+package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;
+
+import static org.junit.jupiter.api.Assertions.*;
+import org.junit.jupiter.api.Test;
+import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
+import java.math.BigDecimal;
+import java.time.LocalDate;
+
+class BolsaTest {
+    private Bolsa nova() {
+        return Bolsa.conceder(new BolsaId(1), new EstudanteId(1), TipoBolsa.MERITO,
+                new BigDecimal("50"), LocalDate.of(2025, 12, 31));
+    }
+
+    @Test void concedeAtiva() {
+        var b = nova();
+        assertEquals(StatusBolsa.ATIVA, b.getStatus());
+        assertEquals(0, new BigDecimal("50").compareTo(b.getPercentual()));
+    }
+
+    @Test void percentualForaDoIntervaloRejeitado() {
+        assertThrows(IllegalArgumentException.class, () -> Bolsa.conceder(new BolsaId(1),
+                new EstudanteId(1), TipoBolsa.MERITO, new BigDecimal("150"), LocalDate.of(2025, 12, 31)));
+        assertThrows(IllegalArgumentException.class, () -> Bolsa.conceder(new BolsaId(1),
+                new EstudanteId(1), TipoBolsa.MERITO, new BigDecimal("-1"), LocalDate.of(2025, 12, 31)));
+    }
+
+    @Test void validadeNulaRejeitada() {
+        assertThrows(IllegalArgumentException.class, () -> Bolsa.conceder(new BolsaId(1),
+                new EstudanteId(1), TipoBolsa.MERITO, new BigDecimal("50"), null));
+    }
+
+    @Test void suspenderEReativar() {
+        var b = nova();
+        b.suspender();
+        assertEquals(StatusBolsa.SUSPENSA, b.getStatus());
+        b.reativar();
+        assertEquals(StatusBolsa.ATIVA, b.getStatus());
+    }
+
+    @Test void naoSuspendeJaSuspensa() {
+        var b = nova();
+        b.suspender();
+        assertThrows(IllegalStateException.class, b::suspender);
+    }
+
+    @Test void naoReativaQuemNaoEstaSuspensa() {
+        var b = nova();
+        assertThrows(IllegalStateException.class, b::reativar);
+    }
+
+    @Test void solicitarRenovacaoExigeAtiva() {
+        var b = nova();
+        b.solicitarRenovacao();
+        assertEquals(StatusBolsa.EM_RENOVACAO, b.getStatus());
+        assertThrows(IllegalStateException.class, b::solicitarRenovacao);
+    }
+
+    @Test void renovarEstendeValidadeEReativa() {
+        var b = nova();
+        b.solicitarRenovacao();
+        b.renovar(LocalDate.of(2026, 12, 31));
+        assertEquals(StatusBolsa.ATIVA, b.getStatus());
+        assertEquals(LocalDate.of(2026, 12, 31), b.getValidade());
+    }
+
+    @Test void renovarRejeitaValidadeNaoPosterior() {
+        var b = nova();
+        assertThrows(IllegalArgumentException.class, () -> b.renovar(LocalDate.of(2024, 1, 1)));
+    }
+
+    @Test void naoRenovaSuspensa() {
+        var b = nova();
+        b.suspender();
+        assertThrows(IllegalStateException.class, () -> b.renovar(LocalDate.of(2026, 12, 31)));
+    }
+}
diff --git a/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/CicloVidaBolsaSteps.java b/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/CicloVidaBolsaSteps.java
new file mode 100644
index 0000000..38e8d5a
--- /dev/null
+++ b/dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/CicloVidaBolsaSteps.java
@@ -0,0 +1,46 @@
+package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;
+
+import io.cucumber.java.pt.Dado;
+import io.cucumber.java.pt.Quando;
+import io.cucumber.java.pt.Entao;
+import io.cucumber.java.pt.E;
+import org.junit.jupiter.api.Assertions;
+import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
+import java.math.BigDecimal;
+import java.time.LocalDate;
+
+public class CicloVidaBolsaSteps {
+    private final BolsaFuncionalidade ctx;
+    public CicloVidaBolsaSteps(BolsaFuncionalidade ctx) { this.ctx = ctx; }
+
+    @Quando("concedo uma bolsa {word} de {int} por cento ao estudante {int} com validade {string}")
+    public void concedo(String tipo, int pct, int estudante, String validade) {
+        var b = ctx.servico.conceder(new EstudanteId(estudante), TipoBolsa.valueOf(tipo),
+                new BigDecimal(pct), LocalDate.parse(validade));
+        ctx.ultimaBolsa = b.getId();
+    }
+
+    @Dado("uma bolsa ATIVA do estudante {int}")
+    public void bolsaAtiva(int estudante) {
+        var b = ctx.servico.conceder(new EstudanteId(estudante), TipoBolsa.MERITO,
+                new BigDecimal("50"), LocalDate.of(2025, 12, 31));
+        ctx.ultimaBolsa = b.getId();
+    }
+
+    @E("a bolsa está suspensa")
+    public void jaSuspensa() { ctx.servico.suspender(ctx.ultimaBolsa); }
+
+    @Quando("suspendo a bolsa")
+    public void suspendo() { ctx.servico.suspender(ctx.ultimaBolsa); }
+
+    @Quando("reativo a bolsa")
+    public void reativo() { ctx.servico.reativar(ctx.ultimaBolsa); }
+
+    @Quando("solicito a renovação da bolsa")
+    public void solicitoRenovacao() { ctx.servico.solicitarRenovacao(ctx.ultimaBolsa); }
+
+    @Entao("a bolsa está com status {string}")
+    public void statusEsperado(String status) {
+        Assertions.assertEquals(StatusBolsa.valueOf(status), ctx.repositorio.obter(ctx.ultimaBolsa).getStatus());
+    }
+}
diff --git a/dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/aplicar_desconto_com_bolsa.feature b/dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/aplicar_desconto_com_bolsa.feature
new file mode 100644
index 0000000..ada439d
--- /dev/null
+++ b/dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/aplicar_desconto_com_bolsa.feature
@@ -0,0 +1,14 @@
+# language: pt
+
+Funcionalidade: Aplicar desconto respaldado por bolsa (RN5)
+
+  Cenário: Desconto aceito com bolsa ativa
+    Dado uma bolsa MERITO ativa de 10 por cento para o estudante 7
+    E uma cobrança aberta de 1000.00 para o estudante 7 contra o contrato 70
+    Quando aplico o desconto da bolsa ativa na cobrança
+    Então o valor atual da cobrança deve ser 900.00 reais
+
+  Cenário: Desconto recusado sem bolsa
+    E uma cobrança aberta de 1000.00 para o estudante 8 contra o contrato 80
+    Quando tento aplicar um desconto de 10 por cento com a autorização "999"
+    Então o desconto é recusado por autorização inválida
diff --git a/dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/conceder_bolsa.feature b/dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/conceder_bolsa.feature
new file mode 100644
index 0000000..805c811
--- /dev/null
+++ b/dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/conceder_bolsa.feature
@@ -0,0 +1,7 @@
+# language: pt
+
+Funcionalidade: Conceder bolsa
+
+  Cenário: Conceder bolsa de mérito ativa
+    Quando concedo uma bolsa MERITO de 50 por cento ao estudante 1 com validade "2025-12-31"
+    Então a bolsa está com status "ATIVA"
diff --git a/dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/renovar_bolsa.feature b/dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/renovar_bolsa.feature
new file mode 100644
index 0000000..6c4c2fd
--- /dev/null
+++ b/dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/renovar_bolsa.feature
@@ -0,0 +1,8 @@
+# language: pt
+
+Funcionalidade: Renovar bolsa
+
+  Cenário: Solicitar renovação coloca a bolsa em renovação
+    Dado uma bolsa ATIVA do estudante 1
+    Quando solicito a renovação da bolsa
+    Então a bolsa está com status "EM_RENOVACAO"
diff --git a/dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/suspender_reativar_bolsa.feature b/dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/suspender_reativar_bolsa.feature
new file mode 100644
index 0000000..05782f9
--- /dev/null
+++ b/dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/suspender_reativar_bolsa.feature
@@ -0,0 +1,14 @@
+# language: pt
+
+Funcionalidade: Suspender e reativar bolsa
+
+  Cenário: Suspender uma bolsa ativa
+    Dado uma bolsa ATIVA do estudante 1
+    Quando suspendo a bolsa
+    Então a bolsa está com status "SUSPENSA"
+
+  Cenário: Reativar uma bolsa suspensa
+    Dado uma bolsa ATIVA do estudante 1
+    E a bolsa está suspensa
+    Quando reativo a bolsa
+    Então a bolsa está com status "ATIVA"

============================================================
## UNIT 2 — APP + INFRA + CONTROLLER (Tasks 4-7)
============================================================
# U2 Review Package — F-13 Sub-A app+infra+controller (Tasks 4-7)

No commits (per user instruction). NEW files diffed vs /dev/null; MODIFIED files diffed to show ONLY U2's changes.

## Changed files
  NEW: BolsaResumo.java, BolsaRepositorioAplicacao.java, BolsaServicoAplicacao.java, BolsaJpa.java, V5__bolsas.sql, VerificadorMatriculaConfirmadaJpa.java, BolsaControlador.java
  MODIFIED: MatriculaJpa.java (clean→now, via git diff HEAD), BackendAplicacao.java (vs pre-Sub-A baseline)

## === NEW FILES (vs /dev/null) ===
diff --git a/aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/BolsaResumo.java b/aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/BolsaResumo.java
new file mode 100644
index 0000000..a48aa7b
--- /dev/null
+++ b/aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/BolsaResumo.java
@@ -0,0 +1,7 @@
+package school.cesar.acadlab.aplicacao.gestaofinanceira;
+
+import java.math.BigDecimal;
+import java.time.LocalDate;
+
+public record BolsaResumo(int id, int estudanteId, String tipo, BigDecimal percentual,
+        LocalDate validade, String status) {}
diff --git a/aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/BolsaRepositorioAplicacao.java b/aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/BolsaRepositorioAplicacao.java
new file mode 100644
index 0000000..6c517a0
--- /dev/null
+++ b/aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/BolsaRepositorioAplicacao.java
@@ -0,0 +1,7 @@
+package school.cesar.acadlab.aplicacao.gestaofinanceira;
+
+import java.util.List;
+
+public interface BolsaRepositorioAplicacao {
+    List<BolsaResumo> listarResumos();
+}
diff --git a/aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/BolsaServicoAplicacao.java b/aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/BolsaServicoAplicacao.java
new file mode 100644
index 0000000..38e4161
--- /dev/null
+++ b/aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/BolsaServicoAplicacao.java
@@ -0,0 +1,15 @@
+package school.cesar.acadlab.aplicacao.gestaofinanceira;
+
+import static org.apache.commons.lang3.Validate.notNull;
+import java.util.List;
+
+public class BolsaServicoAplicacao {
+    private final BolsaRepositorioAplicacao repositorio;
+
+    public BolsaServicoAplicacao(BolsaRepositorioAplicacao repositorio) {
+        notNull(repositorio, "repositório obrigatório");
+        this.repositorio = repositorio;
+    }
+
+    public List<BolsaResumo> listar() { return repositorio.listarResumos(); }
+}
diff --git a/infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/BolsaJpa.java b/infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/BolsaJpa.java
new file mode 100644
index 0000000..db0fe15
--- /dev/null
+++ b/infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/BolsaJpa.java
@@ -0,0 +1,83 @@
+package school.cesar.acadlab.infraestrutura.persistencia.jpa;
+
+import java.math.BigDecimal;
+import java.time.LocalDate;
+import java.util.List;
+
+import org.springframework.beans.factory.annotation.Autowired;
+import org.springframework.data.jpa.repository.JpaRepository;
+import org.springframework.data.jpa.repository.Query;
+import org.springframework.stereotype.Repository;
+
+import jakarta.persistence.Entity;
+import jakarta.persistence.EnumType;
+import jakarta.persistence.Enumerated;
+import jakarta.persistence.Id;
+import jakarta.persistence.Table;
+import school.cesar.acadlab.aplicacao.gestaofinanceira.BolsaRepositorioAplicacao;
+import school.cesar.acadlab.aplicacao.gestaofinanceira.BolsaResumo;
+import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
+import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.Bolsa;
+import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.BolsaId;
+import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.BolsaRepositorio;
+import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.StatusBolsa;
+import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.TipoBolsa;
+
+@Entity
+@Table(name = "BOLSA")
+class BolsaJpa {
+    @Id
+    int id;
+    int estudanteId;
+    @Enumerated(EnumType.STRING)
+    TipoBolsa tipo;
+    BigDecimal percentual;
+    LocalDate validade;
+    @Enumerated(EnumType.STRING)
+    StatusBolsa status;
+}
+
+interface BolsaJpaRepository extends JpaRepository<BolsaJpa, Integer> {
+    @Query("SELECT COALESCE(MAX(b.id), 0) + 1 FROM BolsaJpa b")
+    int proximoId();
+}
+
+@Repository
+class BolsaRepositorioImpl implements BolsaRepositorio, BolsaRepositorioAplicacao {
+    @Autowired
+    BolsaJpaRepository repositorio;
+
+    @Override
+    public BolsaId proximoId() { return new BolsaId(repositorio.proximoId()); }
+
+    @Override
+    public void salvar(Bolsa bolsa) {
+        var jpa = new BolsaJpa();
+        jpa.id = bolsa.getId().valor();
+        jpa.estudanteId = bolsa.getEstudanteId().valor();
+        jpa.tipo = bolsa.getTipo();
+        jpa.percentual = bolsa.getPercentual();
+        jpa.validade = bolsa.getValidade();
+        jpa.status = bolsa.getStatus();
+        repositorio.save(jpa);
+    }
+
+    @Override
+    public Bolsa obter(BolsaId id) { return toDomain(repositorio.findById(id.valor()).orElseThrow()); }
+
+    @Override
+    public List<Bolsa> listar() { return repositorio.findAll().stream().map(this::toDomain).toList(); }
+
+    @Override
+    public List<BolsaResumo> listarResumos() {  // BolsaRepositorioAplicacao
+        return repositorio.findAll().stream()
+                .map(jpa -> new BolsaResumo(jpa.id, jpa.estudanteId, jpa.tipo.name(),
+                        jpa.percentual, jpa.validade, jpa.status.name()))
+                .toList();
+    }
+
+    private Bolsa toDomain(BolsaJpa jpa) {
+        return Bolsa.reconstituir(new BolsaId(jpa.id), new EstudanteId(jpa.estudanteId),
+                jpa.tipo, jpa.percentual, jpa.validade, jpa.status);
+    }
+}
diff --git a/apresentacao-backend/src/main/resources/db/migration/V5__bolsas.sql b/apresentacao-backend/src/main/resources/db/migration/V5__bolsas.sql
new file mode 100644
index 0000000..befdfc8
--- /dev/null
+++ b/apresentacao-backend/src/main/resources/db/migration/V5__bolsas.sql
@@ -0,0 +1,7 @@
+-- ─── BOLSAS (F-13 sub-A) ─────────────────────────────────────────────────────
+INSERT INTO bolsa (id, estudante_id, tipo, percentual, validade, status) VALUES
+  (1, 1, 'MERITO',   50.00, '2025-12-31', 'ATIVA'),
+  (2, 2, 'PROUNI',  100.00, '2026-12-31', 'ATIVA'),
+  (3, 3, 'FIES',     75.00, '2025-06-30', 'EM_RENOVACAO'),
+  (4, 4, 'CONVENIO', 20.00, '2024-12-31', 'SUSPENSA')
+ON CONFLICT (id) DO NOTHING;
diff --git a/infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/VerificadorMatriculaConfirmadaJpa.java b/infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/VerificadorMatriculaConfirmadaJpa.java
new file mode 100644
index 0000000..fe243a6
--- /dev/null
+++ b/infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/VerificadorMatriculaConfirmadaJpa.java
@@ -0,0 +1,21 @@
+package school.cesar.acadlab.infraestrutura.persistencia.jpa;
+
+import org.springframework.beans.factory.annotation.Autowired;
+import org.springframework.stereotype.Component;
+
+import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
+import school.cesar.acadlab.dominio.gestaofinanceira.PeriodoLetivoId;
+import school.cesar.acadlab.dominio.gestaofinanceira.VerificadorMatriculaConfirmada;
+import school.cesar.acadlab.dominio.matricula.matricula.StatusMatricula;
+
+@Component
+class VerificadorMatriculaConfirmadaJpa implements VerificadorMatriculaConfirmada {
+    @Autowired
+    MatriculaJpaRepository repositorio;
+
+    @Override
+    public boolean possuiMatricula(EstudanteId estudanteId, PeriodoLetivoId periodoLetivoId) {
+        return repositorio.existsByEstudanteIdAndPeriodoLetivoIdAndStatus(
+                estudanteId.valor(), periodoLetivoId.valor(), StatusMatricula.CONFIRMADA);
+    }
+}
diff --git a/apresentacao-backend/src/main/java/school/cesar/acadlab/apresentacao/gestaofinanceira/BolsaControlador.java b/apresentacao-backend/src/main/java/school/cesar/acadlab/apresentacao/gestaofinanceira/BolsaControlador.java
new file mode 100644
index 0000000..6a1771d
--- /dev/null
+++ b/apresentacao-backend/src/main/java/school/cesar/acadlab/apresentacao/gestaofinanceira/BolsaControlador.java
@@ -0,0 +1,53 @@
+package school.cesar.acadlab.apresentacao.gestaofinanceira;
+
+import static org.springframework.web.bind.annotation.RequestMethod.GET;
+import static org.springframework.web.bind.annotation.RequestMethod.POST;
+
+import java.math.BigDecimal;
+import java.time.LocalDate;
+import java.util.List;
+
+import org.springframework.beans.factory.annotation.Autowired;
+import org.springframework.web.bind.annotation.PathVariable;
+import org.springframework.web.bind.annotation.RequestBody;
+import org.springframework.web.bind.annotation.RequestMapping;
+import org.springframework.web.bind.annotation.RestController;
+
+import school.cesar.acadlab.aplicacao.gestaofinanceira.BolsaResumo;
+import school.cesar.acadlab.aplicacao.gestaofinanceira.BolsaServicoAplicacao;
+import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
+import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.BolsaId;
+import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.BolsaServico;
+import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.TipoBolsa;
+
+@RestController
+@RequestMapping("backend/bolsas")
+class BolsaControlador {
+    @Autowired
+    private BolsaServico servico;
+
+    @Autowired
+    private BolsaServicoAplicacao servicoAplicacao;
+
+    @RequestMapping(method = GET)
+    List<BolsaResumo> listar() {
+        return servicoAplicacao.listar();
+    }
+
+    @RequestMapping(method = POST, path = "conceder")
+    void conceder(@RequestBody ConcederRequest request) {
+        servico.conceder(new EstudanteId(request.estudanteId()), TipoBolsa.valueOf(request.tipo()),
+                request.percentual(), request.validade());
+    }
+
+    @RequestMapping(method = POST, path = "{id}/suspender")
+    void suspender(@PathVariable int id) { servico.suspender(new BolsaId(id)); }
+
+    @RequestMapping(method = POST, path = "{id}/reativar")
+    void reativar(@PathVariable int id) { servico.reativar(new BolsaId(id)); }
+
+    @RequestMapping(method = POST, path = "{id}/renovar")
+    void renovar(@PathVariable int id) { servico.solicitarRenovacao(new BolsaId(id)); }
+
+    record ConcederRequest(int estudanteId, String tipo, BigDecimal percentual, LocalDate validade) {}
+}

## === MODIFIED: MatriculaJpa.java (git diff HEAD) ===
diff --git a/infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/MatriculaJpa.java b/infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/MatriculaJpa.java
index f22a5bc..81ddf71 100644
--- a/infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/MatriculaJpa.java
+++ b/infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/MatriculaJpa.java
@@ -104,6 +104,8 @@ interface MatriculaJpaRepository extends JpaRepository<MatriculaJpa, Integer> {
 
     @Query("SELECT COALESCE(MAX(m.id), 0) + 1 FROM MatriculaJpa m")
     int proximoId();
+
+    boolean existsByEstudanteIdAndPeriodoLetivoIdAndStatus(int estudanteId, int periodoLetivoId, StatusMatricula status);
 }
 
 @Repository

## === MODIFIED: BackendAplicacao.java (vs pre-Sub-A baseline = ONLY U2 changes) ===
diff --git a/.superpowers/sdd/baseline/subA/BackendAplicacao.java.base b/apresentacao-backend/src/main/java/school/cesar/acadlab/BackendAplicacao.java
index 12f1be5..4c0601f 100644
--- a/.superpowers/sdd/baseline/subA/BackendAplicacao.java.base
+++ b/apresentacao-backend/src/main/java/school/cesar/acadlab/BackendAplicacao.java
@@ -22,8 +22,17 @@ import school.cesar.acadlab.aplicacao.estagios.EstagioRepositorioAplicacao;
 import school.cesar.acadlab.aplicacao.estagios.EstagioServicoAplicacao;
 import school.cesar.acadlab.aplicacao.estagios.OportunidadeRepositorioAplicacao;
 import school.cesar.acadlab.aplicacao.estagios.OportunidadeServicoAplicacao;
+import school.cesar.acadlab.aplicacao.gestaofinanceira.BolsaRepositorioAplicacao;
+import school.cesar.acadlab.aplicacao.gestaofinanceira.BolsaServicoAplicacao;
 import school.cesar.acadlab.aplicacao.gestaofinanceira.CobrancaRepositorioAplicacao;
 import school.cesar.acadlab.aplicacao.gestaofinanceira.CobrancaServicoAplicacao;
+import school.cesar.acadlab.dominio.gestaofinanceira.CobrancaServico;
+import school.cesar.acadlab.dominio.gestaofinanceira.VerificadorAutorizacaoDesconto;
+import school.cesar.acadlab.dominio.gestaofinanceira.VerificadorMatriculaConfirmada;
+import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.CobrancaRepositorio;
+import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.AutorizacaoDescontoPorBolsa;
+import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.BolsaRepositorio;
+import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.BolsaServico;
 import school.cesar.acadlab.aplicacao.gestaopedagogica.DiarioTurmaRepositorioAplicacao;
 import school.cesar.acadlab.aplicacao.gestaopedagogica.DiarioTurmaServicoAplicacao;
 import school.cesar.acadlab.aplicacao.integralizacao.ColacaoRepositorioAplicacao;
@@ -121,6 +130,29 @@ public class BackendAplicacao {
         return new CobrancaServicoAplicacao(repositorio);
     }
 
+    @Bean
+    BolsaServico bolsaServico(BolsaRepositorio repositorio, EventoBarramento barramento) {
+        return new BolsaServico(repositorio, barramento);
+    }
+
+    @Bean
+    BolsaServicoAplicacao bolsaServicoAplicacao(BolsaRepositorioAplicacao repositorio) {
+        return new BolsaServicoAplicacao(repositorio);
+    }
+
+    @Bean
+    VerificadorAutorizacaoDesconto verificadorAutorizacaoDesconto(BolsaRepositorio repositorio) {
+        return new AutorizacaoDescontoPorBolsa(repositorio);
+    }
+
+    @Bean
+    CobrancaServico cobrancaServico(CobrancaRepositorio repositorio,
+            VerificadorMatriculaConfirmada verificadorMatricula,
+            VerificadorAutorizacaoDesconto verificadorAutorizacao,
+            EventoBarramento barramento) {
+        return new CobrancaServico(repositorio, verificadorMatricula, verificadorAutorizacao, barramento);
+    }
+
     @Bean
     DiarioTurmaServico diarioTurmaServico(DiarioTurmaRepositorio repositorio) {
         return new DiarioTurmaServico(repositorio);

============================================================
## UNIT 3 — FRONTEND (Tasks 8-9)
============================================================
# U3 Review Package — F-13 Sub-A frontend (Tasks 8-9)

No commits. Each file diffed against its PRE-Sub-A baseline snapshot, so ONLY U3's changes show (Sub-0 changes are in the baseline and excluded).

## === format.ts (formatValidade added) ===
diff --git a/.superpowers/sdd/baseline/subA/format.ts.base b/apresentacao-frontend/react/src/lib/format.ts
index 32e5350..5727f4b 100644
--- a/.superpowers/sdd/baseline/subA/format.ts.base
+++ b/apresentacao-frontend/react/src/lib/format.ts
@@ -21,6 +21,14 @@ export function agoraParaInput(): string {
   return local.toISOString().slice(0, 16);
 }
 
+/** Converte data ISO (YYYY-MM-DD) para "MM/AAAA". Retorna "—" se ausente. */
+export function formatValidade(iso: string | null | undefined): string {
+  if (!iso) return "—";
+  const [ano, mes] = iso.split("-");
+  if (!ano || !mes) return iso;
+  return `${mes}/${ano}`;
+}
+
 /** Formata número/decimal do backend como moeda BRL (ex.: 1420 → "R$ 1.420,00"). */
 export function formatMoeda(valor: number | string | null | undefined): string {
   if (valor === null || valor === undefined) return "—";

## === financeiro.ts (bolsa types + 5 hooks appended) ===
diff --git a/.superpowers/sdd/baseline/subA/financeiro.ts.base b/apresentacao-frontend/react/src/lib/financeiro.ts
index 036e62b..e727705 100644
--- a/.superpowers/sdd/baseline/subA/financeiro.ts.base
+++ b/apresentacao-frontend/react/src/lib/financeiro.ts
@@ -105,3 +105,48 @@ export function useResolverContestacao() {
     onSuccess: invalidate,
   });
 }
+
+/* ===== Bolsas (sub-projeto A) ===== */
+
+export type TipoBolsa = "PROUNI" | "FIES" | "MERITO" | "CONVENIO";
+export type StatusBolsa = "ATIVA" | "SUSPENSA" | "EM_RENOVACAO";
+
+export type BolsaResumo = {
+  id: number;
+  estudanteId: number;
+  tipo: TipoBolsa;
+  percentual: number;
+  validade: string | null;
+  status: StatusBolsa;
+};
+
+export function useBolsas() {
+  return useQuery({
+    queryKey: ["financeiro", "bolsas"] as const,
+    queryFn: () => api.get<BolsaResumo[]>("bolsas"),
+  });
+}
+
+export function useConcederBolsa() {
+  const invalidate = useInvalidate();
+  return useMutation({
+    mutationFn: (vars: { estudanteId: number; tipo: TipoBolsa; percentual: number; validade: string }) =>
+      api.post("bolsas/conceder", vars),
+    onSuccess: invalidate,
+  });
+}
+
+export function useSuspenderBolsa() {
+  const invalidate = useInvalidate();
+  return useMutation({ mutationFn: (id: number) => api.post(`bolsas/${id}/suspender`), onSuccess: invalidate });
+}
+
+export function useReativarBolsa() {
+  const invalidate = useInvalidate();
+  return useMutation({ mutationFn: (id: number) => api.post(`bolsas/${id}/reativar`), onSuccess: invalidate });
+}
+
+export function useRenovarBolsa() {
+  const invalidate = useInvalidate();
+  return useMutation({ mutationFn: (id: number) => api.post(`bolsas/${id}/renovar`), onSuccess: invalidate });
+}

## === financeiro.tsx (bolsas tab rewired — MUST be data/types/handlers only) ===
diff --git a/.superpowers/sdd/baseline/subA/financeiro.tsx.base b/apresentacao-frontend/react/src/routes/financeiro.tsx
index 7df7be2..f7bf261 100644
--- a/.superpowers/sdd/baseline/subA/financeiro.tsx.base
+++ b/apresentacao-frontend/react/src/routes/financeiro.tsx
@@ -13,9 +13,10 @@ import { toast } from "sonner";
 import {
   useExtrato, useRegistrarPagamento, useContestar,
   useContestacoesAbertas, useResolverContestacao,
-  type CobrancaResumo, type StatusCobranca,
+  useBolsas, useConcederBolsa, useSuspenderBolsa, useReativarBolsa, useRenovarBolsa,
+  type CobrancaResumo, type StatusCobranca, type BolsaResumo, type TipoBolsa, type StatusBolsa,
 } from "@/lib/financeiro";
-import { formatData, formatMoeda } from "@/lib/format";
+import { formatData, formatMoeda, formatValidade } from "@/lib/format";
 import { hojeIso } from "@/lib/api";
 import { USUARIO_ATUAL } from "@/lib/config";
 
@@ -201,7 +202,6 @@ function Page() {
 }
 
 type Inadimplente = { matricula: string; aluno: string; curso: string; emAtraso: string; diasAtraso: number; status: "Notificado" | "Negociar" | "Bloqueado" };
-type Bolsa = { id: string; aluno: string; tipo: "ProUni" | "FIES" | "Mérito" | "Convênio"; percentual: number; validade: string; status: "Ativa" | "Em renovação" | "Suspensa" };
 type Lancamento = { id: string; data: string; descricao: string; metodo: "PIX" | "Boleto" | "Cartão"; valor: string; status: "Conciliado" | "Pendente" | "Divergente" };
 
 function FinanceiroView() {
@@ -217,12 +217,14 @@ function FinanceiroView() {
     { matricula: "2020.0712", aluno: "Rafael Lima", curso: "Medicina", emAtraso: "R$ 9.120,00", diasAtraso: 121, status: "Bloqueado" },
     { matricula: "2023.0034", aluno: "Beatriz Souza", curso: "Psicologia", emAtraso: "R$ 1.420,00", diasAtraso: 18, status: "Notificado" },
   ]);
-  const [bolsas, setBolsas] = useState<Bolsa[]>([
-    { id: "BL-441", aluno: "Maria Santos", tipo: "Mérito", percentual: 50, validade: "12/2025", status: "Ativa" },
-    { id: "BL-318", aluno: "João Oliveira", tipo: "ProUni", percentual: 100, validade: "12/2026", status: "Ativa" },
-    { id: "BL-205", aluno: "Ana Costa", tipo: "FIES", percentual: 75, validade: "06/2025", status: "Em renovação" },
-    { id: "BL-099", aluno: "Bruno Dias", tipo: "Convênio", percentual: 20, validade: "12/2024", status: "Suspensa" },
-  ]);
+  const bolsasQuery = useBolsas();
+  const bolsas = bolsasQuery.data ?? [];
+  const concederBolsa = useConcederBolsa();
+  const suspenderBolsa = useSuspenderBolsa();
+  const reativarBolsa = useReativarBolsa();
+  const renovarBolsa = useRenovarBolsa();
+  const rotuloStatusBolsa: Record<StatusBolsa, string> = { ATIVA: "Ativa", EM_RENOVACAO: "Em renovação", SUSPENSA: "Suspensa" };
+  const tomStatusBolsa = (s: StatusBolsa) => (s === "ATIVA" ? "success" : s === "SUSPENSA" ? "danger" : "warning");
   const [lancs, setLancs] = useState<Lancamento[]>([
     { id: "LC-9821", data: "18/03/2025", descricao: "PIX recebido — 2021.0188", metodo: "PIX", valor: "R$ 1.420,00", status: "Conciliado" },
     { id: "LC-9820", data: "18/03/2025", descricao: "Boleto compensado — 2022.0345", metodo: "Boleto", valor: "R$ 1.420,00", status: "Conciliado" },
@@ -231,6 +233,10 @@ function FinanceiroView() {
   ]);
   const [filtroInad, setFiltroInad] = useState("");
   const [novaBolsaOpen, setNovaBolsaOpen] = useState(false);
+  const [novaMatricula, setNovaMatricula] = useState("");
+  const [novoTipo, setNovoTipo] = useState<TipoBolsa>("MERITO");
+  const [novoPercentual, setNovoPercentual] = useState("");
+  const [novaValidade, setNovaValidade] = useState("");
 
   const notificar = (m: string) => {
     setInadimp((p) => p.map((i) => i.matricula === m ? { ...i, status: "Notificado" } : i));
@@ -248,14 +254,6 @@ function FinanceiroView() {
     setLancs((p) => p.map((l) => l.id === id ? { ...l, status: "Conciliado" } : l));
     toast.success(`Lançamento ${id} conciliado.`);
   };
-  const suspenderBolsa = (id: string) => {
-    setBolsas((p) => p.map((b) => b.id === id ? { ...b, status: "Suspensa" } : b));
-    toast.warning(`Bolsa ${id} suspensa.`);
-  };
-  const reativarBolsa = (id: string) => {
-    setBolsas((p) => p.map((b) => b.id === id ? { ...b, status: "Ativa" } : b));
-    toast.success(`Bolsa ${id} reativada.`);
-  };
 
   const inadimpFiltrada = useMemo(
     () => inadimp.filter((i) => (i.aluno + i.matricula + i.curso).toLowerCase().includes(filtroInad.toLowerCase())),
@@ -268,7 +266,7 @@ function FinanceiroView() {
         { label: "Arrecadação no mês", value: "R$ 1,82M", tone: "success" },
         { label: "Inadimplência", value: `${inadimp.length} alunos`, tone: "warning" },
         { label: "Contestações abertas", value: contestacoesAbertas.length, tone: "info" },
-        { label: "Bolsas ativas", value: bolsas.filter((b) => b.status === "Ativa").length, tone: "info" },
+        { label: "Bolsas ativas", value: bolsas.filter((b) => b.status === "ATIVA").length, tone: "info" },
       ]} />
 
       <TabsRow
@@ -361,38 +359,36 @@ function FinanceiroView() {
             <div className="rounded-xl border bg-card p-5 shadow-card">
               <SectionTitle title="Conceder bolsa" subtitle="Preencha os dados da nova concessão." />
               <div className="mt-3 grid grid-cols-2 gap-3">
-                <FormField label="Matrícula" required><Input placeholder="2024.XXXX" /></FormField>
-                <FormField label="Tipo" required><Input placeholder="Mérito, ProUni, FIES…" /></FormField>
-                <FormField label="Percentual (%)" required><Input type="number" placeholder="0–100" /></FormField>
-                <FormField label="Validade" required><Input placeholder="MM/AAAA" /></FormField>
+                <FormField label="Matrícula" required><Input placeholder="2024.XXXX" value={novaMatricula} onChange={(e) => setNovaMatricula(e.target.value)} /></FormField>
+                <FormField label="Tipo" required><Input placeholder="MERITO, PROUNI, FIES, CONVENIO" value={novoTipo} onChange={(e) => setNovoTipo(e.target.value as TipoBolsa)} /></FormField>
+                <FormField label="Percentual (%)" required><Input type="number" placeholder="0–100" value={novoPercentual} onChange={(e) => setNovoPercentual(e.target.value)} /></FormField>
+                <FormField label="Validade" required><Input type="date" value={novaValidade} onChange={(e) => setNovaValidade(e.target.value)} /></FormField>
               </div>
               <div className="mt-4 flex justify-end gap-2">
                 <Button variant="outline" onClick={() => setNovaBolsaOpen(false)}>Cancelar</Button>
-                <Button onClick={() => {
-                  const id = `BL-${Math.floor(Math.random() * 900 + 100)}`;
-                  setBolsas((p) => [{ id, aluno: "Novo Beneficiário", tipo: "Mérito", percentual: 25, validade: "12/2025", status: "Ativa" }, ...p]);
-                  setNovaBolsaOpen(false);
-                  toast.success(`Bolsa ${id} concedida.`);
-                }}>Conceder</Button>
+                <Button onClick={() => concederBolsa.mutate(
+                  { estudanteId: Number(novaMatricula), tipo: novoTipo, percentual: Number(novoPercentual), validade: novaValidade },
+                  { onSuccess: () => { setNovaBolsaOpen(false); toast.success("Bolsa concedida."); } },
+                )}>Conceder</Button>
               </div>
             </div>
           )}
           <DataTable
             columns={[
-              { key: "id", header: "Código" },
-              { key: "aluno", header: "Beneficiário" },
+              { key: "id", header: "Código", render: (r) => `BL-${r.id}` },
+              { key: "estudante", header: "Beneficiário", render: (r) => `Estudante #${r.estudanteId}` },
               { key: "tipo", header: "Tipo" },
               { key: "percentual", header: "%", align: "right", render: (r) => `${r.percentual}%` },
-              { key: "validade", header: "Validade" },
+              { key: "validade", header: "Validade", render: (r) => formatValidade(r.validade) },
               { key: "status", header: "Status", render: (r) => (
-                <StatusBadge tone={r.status === "Ativa" ? "success" : r.status === "Suspensa" ? "danger" : "warning"}>{r.status}</StatusBadge>
+                <StatusBadge tone={tomStatusBolsa(r.status)}>{rotuloStatusBolsa[r.status]}</StatusBadge>
               )},
               { key: "acoes", header: "", align: "right", render: (r) => (
                 <div className="flex justify-end gap-2">
-                  {r.status === "Ativa"
-                    ? <RowActionButton tone="danger" onClick={() => suspenderBolsa(r.id)}>Suspender</RowActionButton>
-                    : <RowActionButton tone="info" onClick={() => reativarBolsa(r.id)}>Reativar</RowActionButton>}
-                  <RowActionButton onClick={() => toast.success(`Renovação de ${r.id} iniciada.`)}>Renovar</RowActionButton>
+                  {r.status === "SUSPENSA"
+                    ? <RowActionButton tone="info" onClick={() => reativarBolsa.mutate(r.id, { onSuccess: () => toast.success(`Bolsa BL-${r.id} reativada.`) })}>Reativar</RowActionButton>
+                    : <RowActionButton tone="danger" onClick={() => suspenderBolsa.mutate(r.id, { onSuccess: () => toast.warning(`Bolsa BL-${r.id} suspensa.`) })}>Suspender</RowActionButton>}
+                  <RowActionButton onClick={() => renovarBolsa.mutate(r.id, { onSuccess: () => toast.success(`Renovação de BL-${r.id} iniciada.`) })}>Renovar</RowActionButton>
                 </div>
               )},
             ]}
