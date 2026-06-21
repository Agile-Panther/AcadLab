### Task 1: Agregado `Bolsa` + enums + eventos (com testes unitários)

**Files:**
- Create: `dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaId.java`
- Create: `.../dominio/gestaofinanceira/bolsa/TipoBolsa.java`
- Create: `.../dominio/gestaofinanceira/bolsa/StatusBolsa.java`
- Create: `.../dominio/gestaofinanceira/bolsa/Bolsa.java`
- Test: `dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/BolsaTest.java`

**Interfaces:**
- Produces (consumido nas Tasks 2-7): `Bolsa` com factory `conceder(BolsaId,EstudanteId,TipoBolsa,BigDecimal,LocalDate)`; métodos `suspender()→BolsaSuspensaEvento`, `reativar()→BolsaReativadaEvento`, `solicitarRenovacao()→RenovacaoSolicitadaEvento`, `renovar(LocalDate)→BolsaRenovadaEvento`, `eventoConcessao()→BolsaConcedidaEvento`; `reconstituir(...)`; getters `getId/getEstudanteId/getTipo/getPercentual/getValidade/getStatus`. Reusa `EstudanteId` de `school.cesar.acadlab.dominio.gestaofinanceira`.

- [ ] **Step 1: Escrever os testes que falham (`BolsaTest`)**

```java
package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
import java.math.BigDecimal;
import java.time.LocalDate;

class BolsaTest {
    private Bolsa nova() {
        return Bolsa.conceder(new BolsaId(1), new EstudanteId(1), TipoBolsa.MERITO,
                new BigDecimal("50"), LocalDate.of(2025, 12, 31));
    }

    @Test void concedeAtiva() {
        var b = nova();
        assertEquals(StatusBolsa.ATIVA, b.getStatus());
        assertEquals(0, new BigDecimal("50").compareTo(b.getPercentual()));
    }

    @Test void percentualForaDoIntervaloRejeitado() {
        assertThrows(IllegalArgumentException.class, () -> Bolsa.conceder(new BolsaId(1),
                new EstudanteId(1), TipoBolsa.MERITO, new BigDecimal("150"), LocalDate.of(2025, 12, 31)));
        assertThrows(IllegalArgumentException.class, () -> Bolsa.conceder(new BolsaId(1),
                new EstudanteId(1), TipoBolsa.MERITO, new BigDecimal("-1"), LocalDate.of(2025, 12, 31)));
    }

    @Test void validadeNulaRejeitada() {
        assertThrows(IllegalArgumentException.class, () -> Bolsa.conceder(new BolsaId(1),
                new EstudanteId(1), TipoBolsa.MERITO, new BigDecimal("50"), null));
    }

    @Test void suspenderEReativar() {
        var b = nova();
        b.suspender();
        assertEquals(StatusBolsa.SUSPENSA, b.getStatus());
        b.reativar();
        assertEquals(StatusBolsa.ATIVA, b.getStatus());
    }

    @Test void naoSuspendeJaSuspensa() {
        var b = nova();
        b.suspender();
        assertThrows(IllegalStateException.class, b::suspender);
    }

    @Test void naoReativaQuemNaoEstaSuspensa() {
        var b = nova();
        assertThrows(IllegalStateException.class, b::reativar);
    }

    @Test void solicitarRenovacaoExigeAtiva() {
        var b = nova();
        b.solicitarRenovacao();
        assertEquals(StatusBolsa.EM_RENOVACAO, b.getStatus());
        assertThrows(IllegalStateException.class, b::solicitarRenovacao);
    }

    @Test void renovarEstendeValidadeEReativa() {
        var b = nova();
        b.solicitarRenovacao();
        b.renovar(LocalDate.of(2026, 12, 31));
        assertEquals(StatusBolsa.ATIVA, b.getStatus());
        assertEquals(LocalDate.of(2026, 12, 31), b.getValidade());
    }

    @Test void renovarRejeitaValidadeNaoPosterior() {
        var b = nova();
        assertThrows(IllegalArgumentException.class, () -> b.renovar(LocalDate.of(2024, 1, 1)));
    }

    @Test void naoRenovaSuspensa() {
        var b = nova();
        b.suspender();
        assertThrows(IllegalStateException.class, () -> b.renovar(LocalDate.of(2026, 12, 31)));
    }
}
```

- [ ] **Step 2: Rodar e ver falhar (compilação)**

Run: `mvn -q -pl dominio-gestao-financeira test -Dtest=BolsaTest`
Expected: FALHA de compilação (classes `Bolsa`/`BolsaId`/`TipoBolsa`/`StatusBolsa` inexistentes).

- [ ] **Step 3: Criar enums e id**

`BolsaId.java`:
```java
package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

public record BolsaId(int valor) {}
```

`TipoBolsa.java`:
```java
package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

public enum TipoBolsa { PROUNI, FIES, MERITO, CONVENIO }
```

`StatusBolsa.java`:
```java
package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

public enum StatusBolsa { ATIVA, SUSPENSA, EM_RENOVACAO }
```

- [ ] **Step 4: Criar o agregado `Bolsa`**

`Bolsa.java`:
```java
package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;

public class Bolsa {
    private static final BigDecimal CEM = new BigDecimal("100");

    private final BolsaId id;
    private final EstudanteId estudanteId;
    private final TipoBolsa tipo;
    private final BigDecimal percentual;
    private LocalDate validade;
    private StatusBolsa status;

    private Bolsa(BolsaId id, EstudanteId estudanteId, TipoBolsa tipo, BigDecimal percentual,
            LocalDate validade, StatusBolsa status) {
        notNull(id, "id obrigatório");
        notNull(estudanteId, "estudanteId obrigatório");
        notNull(tipo, "tipo obrigatório");
        notNull(percentual, "percentual obrigatório");
        isTrue(percentual.compareTo(BigDecimal.ZERO) >= 0 && percentual.compareTo(CEM) <= 0,
                "percentual deve estar entre 0 e 100");
        notNull(validade, "validade obrigatória");
        notNull(status, "status obrigatório");
        this.id = id;
        this.estudanteId = estudanteId;
        this.tipo = tipo;
        this.percentual = percentual;
        this.validade = validade;
        this.status = status;
    }

    public static Bolsa conceder(BolsaId id, EstudanteId estudanteId, TipoBolsa tipo,
            BigDecimal percentual, LocalDate validade) {
        return new Bolsa(id, estudanteId, tipo, percentual, validade, StatusBolsa.ATIVA);
    }

    public static Bolsa reconstituir(BolsaId id, EstudanteId estudanteId, TipoBolsa tipo,
            BigDecimal percentual, LocalDate validade, StatusBolsa status) {
        return new Bolsa(id, estudanteId, tipo, percentual, validade, status);
    }

    public BolsaConcedidaEvento eventoConcessao() { return new BolsaConcedidaEvento(this); }

    public BolsaSuspensaEvento suspender() {
        if (status == StatusBolsa.SUSPENSA)
            throw new IllegalStateException("bolsa já está suspensa");
        this.status = StatusBolsa.SUSPENSA;
        return new BolsaSuspensaEvento(this);
    }

    public BolsaReativadaEvento reativar() {
        if (status != StatusBolsa.SUSPENSA)
            throw new IllegalStateException("só é possível reativar uma bolsa suspensa");
        this.status = StatusBolsa.ATIVA;
        return new BolsaReativadaEvento(this);
    }

    public RenovacaoSolicitadaEvento solicitarRenovacao() {
        if (status != StatusBolsa.ATIVA)
            throw new IllegalStateException("só é possível solicitar renovação de uma bolsa ativa");
        this.status = StatusBolsa.EM_RENOVACAO;
        return new RenovacaoSolicitadaEvento(this);
    }

    public BolsaRenovadaEvento renovar(LocalDate novaValidade) {
        notNull(novaValidade, "novaValidade obrigatória");
        if (status == StatusBolsa.SUSPENSA)
            throw new IllegalStateException("não é possível renovar uma bolsa suspensa");
        isTrue(novaValidade.isAfter(this.validade), "novaValidade deve ser posterior à validade atual");
        this.validade = novaValidade;
        this.status = StatusBolsa.ATIVA;
        return new BolsaRenovadaEvento(this);
    }

    public BolsaId getId() { return id; }
    public EstudanteId getEstudanteId() { return estudanteId; }
    public TipoBolsa getTipo() { return tipo; }
    public BigDecimal getPercentual() { return percentual; }
    public LocalDate getValidade() { return validade; }
    public StatusBolsa getStatus() { return status; }

    /* ===== Eventos (Observer) ===== */
    public abstract static class BolsaEvento {
        private final Bolsa bolsa;
        protected BolsaEvento(Bolsa bolsa) { this.bolsa = bolsa; }
        public Bolsa getBolsa() { return bolsa; }
    }
    public static class BolsaConcedidaEvento extends BolsaEvento {
        private BolsaConcedidaEvento(Bolsa b) { super(b); }
    }
    public static class BolsaSuspensaEvento extends BolsaEvento {
        private BolsaSuspensaEvento(Bolsa b) { super(b); }
    }
    public static class BolsaReativadaEvento extends BolsaEvento {
        private BolsaReativadaEvento(Bolsa b) { super(b); }
    }
    public static class RenovacaoSolicitadaEvento extends BolsaEvento {
        private RenovacaoSolicitadaEvento(Bolsa b) { super(b); }
    }
    public static class BolsaRenovadaEvento extends BolsaEvento {
        private BolsaRenovadaEvento(Bolsa b) { super(b); }
    }
}
```

- [ ] **Step 5: Rodar os testes e ver passar**

Run: `mvn -q -pl dominio-gestao-financeira test -Dtest=BolsaTest`
Expected: PASS (10 testes).

---

