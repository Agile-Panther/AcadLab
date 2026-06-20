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
