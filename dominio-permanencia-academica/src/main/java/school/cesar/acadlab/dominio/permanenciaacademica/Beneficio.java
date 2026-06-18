package school.cesar.acadlab.dominio.permanenciaacademica;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;

// Subject do padrão Observer: notifica ao mudar de status
public class Beneficio {
    private final BeneficioId id;
    private final InscricaoId inscricaoId;
    private final EstudantePermanenciaId estudanteId;
    private final EditalId editalId;
    private StatusBeneficio status;
    private final LocalDate dataAtivacao;
    private final LocalDate prazoRenovacao;
    private boolean solicitouRenovacao;

    public Beneficio(BeneficioId id, InscricaoId inscricaoId, EstudantePermanenciaId estudanteId,
                     EditalId editalId, LocalDate prazoRenovacao) {
        notNull(id, "O id não pode ser nulo");
        notNull(inscricaoId, "O id da inscrição não pode ser nulo");
        notNull(estudanteId, "O estudante não pode ser nulo");
        notNull(editalId, "O edital não pode ser nulo");
        notNull(prazoRenovacao, "O prazo de renovação não pode ser nulo");
        this.id = id;
        this.inscricaoId = inscricaoId;
        this.estudanteId = estudanteId;
        this.editalId = editalId;
        this.status = StatusBeneficio.ATIVO;
        this.dataAtivacao = LocalDate.now();
        this.prazoRenovacao = prazoRenovacao;
        this.solicitouRenovacao = false;
    }

    private Beneficio(BeneficioId id, InscricaoId inscricaoId, EstudantePermanenciaId estudanteId,
                      EditalId editalId, StatusBeneficio status, LocalDate dataAtivacao,
                      LocalDate prazoRenovacao, boolean solicitouRenovacao) {
        this.id = id;
        this.inscricaoId = inscricaoId;
        this.estudanteId = estudanteId;
        this.editalId = editalId;
        this.status = status;
        this.dataAtivacao = dataAtivacao;
        this.prazoRenovacao = prazoRenovacao;
        this.solicitouRenovacao = solicitouRenovacao;
    }

    public static Beneficio reconstituir(BeneficioId id, InscricaoId inscricaoId,
                                          EstudantePermanenciaId estudanteId, EditalId editalId,
                                          StatusBeneficio status, LocalDate dataAtivacao,
                                          LocalDate prazoRenovacao, boolean solicitouRenovacao) {
        return new Beneficio(id, inscricaoId, estudanteId, editalId, status,
                dataAtivacao, prazoRenovacao, solicitouRenovacao);
    }

    // RN8: suspender por não cumprimento dos critérios mínimos — Observer
    public BeneficioSuspensosEvento suspender() {
        if (status != StatusBeneficio.ATIVO) {
            throw new IllegalStateException("Apenas benefícios ativos podem ser suspensos");
        }
        this.status = StatusBeneficio.SUSPENSO;
        return new BeneficioSuspensosEvento(this);
    }

    // RN8: cancelar por não cumprimento dos critérios mínimos — Observer
    public BeneficioCanceladoEvento cancelar() {
        if (status == StatusBeneficio.CANCELADO) {
            throw new IllegalStateException("Benefício já está cancelado");
        }
        this.status = StatusBeneficio.CANCELADO;
        return new BeneficioCanceladoEvento(this);
    }

    // RN7: renovação dentro do prazo verificada no serviço
    public RenovacaoSolicitadaEvento solicitarRenovacao(LocalDate hoje) {
        notNull(hoje, "A data não pode ser nula");
        if (hoje.isAfter(prazoRenovacao)) {
            throw new IllegalStateException("A renovação só pode ser solicitada dentro do prazo definido");
        }
        if (status != StatusBeneficio.ATIVO) {
            throw new IllegalStateException("Apenas benefícios ativos podem ser renovados");
        }
        this.solicitouRenovacao = true;
        return new RenovacaoSolicitadaEvento(this);
    }

    public BeneficioId getId() { return id; }
    public InscricaoId getInscricaoId() { return inscricaoId; }
    public EstudantePermanenciaId getEstudanteId() { return estudanteId; }
    public EditalId getEditalId() { return editalId; }
    public StatusBeneficio getStatus() { return status; }
    public LocalDate getDataAtivacao() { return dataAtivacao; }
    public LocalDate getPrazoRenovacao() { return prazoRenovacao; }
    public boolean isSolicitouRenovacao() { return solicitouRenovacao; }

    public static abstract class BeneficioEvento {
        private final Beneficio beneficio;
        protected BeneficioEvento(Beneficio beneficio) { this.beneficio = beneficio; }
        public Beneficio getBeneficio() { return beneficio; }
    }

    public static class BeneficioSuspensosEvento extends BeneficioEvento {
        private BeneficioSuspensosEvento(Beneficio beneficio) { super(beneficio); }
    }

    public static class BeneficioCanceladoEvento extends BeneficioEvento {
        private BeneficioCanceladoEvento(Beneficio beneficio) { super(beneficio); }
    }

    public static class RenovacaoSolicitadaEvento extends BeneficioEvento {
        private RenovacaoSolicitadaEvento(Beneficio beneficio) { super(beneficio); }
    }
}
