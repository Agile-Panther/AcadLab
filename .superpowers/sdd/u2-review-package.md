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
