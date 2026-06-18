package school.cesar.acadlab.dominio.permanenciaacademica;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;

public class Inscricao {
    private final InscricaoId id;
    private final EditalId editalId;
    private final EstudantePermanenciaId estudanteId;
    private StatusInscricao status;
    private boolean recursoInterposto;
    private int pontuacao;
    private final LocalDate dataInscricao;

    public Inscricao(InscricaoId id, EditalId editalId, EstudantePermanenciaId estudanteId) {
        notNull(id, "O id não pode ser nulo");
        notNull(editalId, "O edital não pode ser nulo");
        notNull(estudanteId, "O estudante não pode ser nulo");
        this.id = id;
        this.editalId = editalId;
        this.estudanteId = estudanteId;
        this.status = StatusInscricao.PENDENTE;
        this.recursoInterposto = false;
        this.dataInscricao = LocalDate.now();
    }

    private Inscricao(InscricaoId id, EditalId editalId, EstudantePermanenciaId estudanteId,
                      StatusInscricao status, boolean recursoInterposto, int pontuacao, LocalDate dataInscricao) {
        this.id = id;
        this.editalId = editalId;
        this.estudanteId = estudanteId;
        this.status = status;
        this.recursoInterposto = recursoInterposto;
        this.pontuacao = pontuacao;
        this.dataInscricao = dataInscricao;
    }

    public static Inscricao reconstituir(InscricaoId id, EditalId editalId, EstudantePermanenciaId estudanteId,
                                         StatusInscricao status, boolean recursoInterposto, int pontuacao,
                                         LocalDate dataInscricao) {
        return new Inscricao(id, editalId, estudanteId, status, recursoInterposto, pontuacao, dataInscricao);
    }

    // RN4: deferimento registrado pelo serviço (valida perfil de assistência estudantil)
    public InscricaoDeferidaEvento deferir(int pontuacao) {
        if (status != StatusInscricao.PENDENTE && status != StatusInscricao.RECURSO_INTERPOSTO) {
            throw new IllegalStateException("Inscrição não está em estado passível de deferimento");
        }
        this.status = StatusInscricao.DEFERIDA;
        this.pontuacao = pontuacao;
        return new InscricaoDeferidaEvento(this);
    }

    public InscricaoIndeferidaEvento indeferir() {
        if (status != StatusInscricao.PENDENTE && status != StatusInscricao.RECURSO_INTERPOSTO) {
            throw new IllegalStateException("Inscrição não está em estado passível de indeferimento");
        }
        this.status = StatusInscricao.INDEFERIDA;
        return new InscricaoIndeferidaEvento(this);
    }

    // RN9: prazo verificado no serviço; RN10: apenas um recurso por inscrição
    public RecursoInterpostoEvento interporRecurso() {
        if (status != StatusInscricao.INDEFERIDA) {
            throw new IllegalStateException("Recurso só pode ser interposto para inscrição indeferida");
        }
        if (recursoInterposto) {
            throw new IllegalStateException("Já foi interposto um recurso para esta inscrição");
        }
        this.status = StatusInscricao.RECURSO_INTERPOSTO;
        this.recursoInterposto = true;
        return new RecursoInterpostoEvento(this);
    }

    public InscricaoId getId() { return id; }
    public EditalId getEditalId() { return editalId; }
    public EstudantePermanenciaId getEstudanteId() { return estudanteId; }
    public StatusInscricao getStatus() { return status; }
    public boolean isRecursoInterposto() { return recursoInterposto; }
    public int getPontuacao() { return pontuacao; }
    public LocalDate getDataInscricao() { return dataInscricao; }

    public static abstract class InscricaoEvento {
        private final Inscricao inscricao;
        protected InscricaoEvento(Inscricao inscricao) { this.inscricao = inscricao; }
        public Inscricao getInscricao() { return inscricao; }
    }

    public static class InscricaoDeferidaEvento extends InscricaoEvento {
        private InscricaoDeferidaEvento(Inscricao inscricao) { super(inscricao); }
    }

    public static class InscricaoIndeferidaEvento extends InscricaoEvento {
        private InscricaoIndeferidaEvento(Inscricao inscricao) { super(inscricao); }
    }

    public static class RecursoInterpostoEvento extends InscricaoEvento {
        private RecursoInterpostoEvento(Inscricao inscricao) { super(inscricao); }
    }
}
