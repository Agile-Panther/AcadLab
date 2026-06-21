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
        if (validade == null) throw new IllegalArgumentException("validade obrigatória");
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
