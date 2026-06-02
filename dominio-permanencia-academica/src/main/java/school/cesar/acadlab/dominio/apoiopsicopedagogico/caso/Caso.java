package school.cesar.acadlab.dominio.apoiopsicopedagogico.caso;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.atendimento.Atendimento;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.Triagem;

public class Caso {
    private final CasoId id;
    private final EstudanteId estudanteId;
    private PsicopedagogoId responsavelId;
    private StatusCaso status;
    private Triagem triagem;
    private final List<Atendimento> atendimentos = new ArrayList<>();

    public Caso(CasoId id, EstudanteId estudanteId) {
        notNull(id, "O id não pode ser nulo");
        notNull(estudanteId, "O estudante não pode ser nulo");
        this.id = id;
        this.estudanteId = estudanteId;
        this.status = StatusCaso.ABERTO;
    }

    public Caso(CasoId id, EstudanteId estudanteId, PsicopedagogoId responsavelId,
                StatusCaso status, Triagem triagem, List<Atendimento> atendimentos) {
        notNull(id, "O id não pode ser nulo");
        notNull(estudanteId, "O estudante não pode ser nulo");
        notNull(status, "O status não pode ser nulo");
        this.id = id;
        this.estudanteId = estudanteId;
        this.responsavelId = responsavelId;
        this.status = status;
        this.triagem = triagem;
        if (atendimentos != null) {
            this.atendimentos.addAll(atendimentos);
        }
    }

    public CasoId getId() { return id; }
    public EstudanteId getEstudanteId() { return estudanteId; }
    public PsicopedagogoId getResponsavelId() { return responsavelId; }
    public StatusCaso getStatus() { return status; }
    public Triagem getTriagem() { return triagem; }
    public List<Atendimento> getAtendimentos() { return Collections.unmodifiableList(atendimentos); }

    // RN2: triagem registrada; define responsável do caso
    public TriagemRealizadaEvento realizarTriagem(Triagem triagem) {
        notNull(triagem, "A triagem não pode ser nula");
        this.triagem = triagem;
        this.responsavelId = triagem.getResponsavelId();
        return new TriagemRealizadaEvento(this);
    }

    // RN2: atendimento exige triagem prévia
    public AtendimentoRegistradoEvento registrarAtendimento(Atendimento atendimento) {
        notNull(atendimento, "O atendimento não pode ser nulo");
        if (triagem == null) {
            throw new IllegalStateException("O caso precisa passar por triagem antes do primeiro atendimento");
        }
        atendimentos.add(atendimento);
        this.status = StatusCaso.EM_ATENDIMENTO;
        return new AtendimentoRegistradoEvento(this, atendimento);
    }

    // RN7: encerramento exige conclusão ou encaminhamento final
    public CasoEncerradoEvento encerrar() {
        boolean temConclusaoOuEncaminhamento = atendimentos.stream()
                .anyMatch(a -> a.isConclusaoFinal()
                        || (a.getEncaminhamento() != null && !a.getEncaminhamento().isBlank()));
        if (!temConclusaoOuEncaminhamento) {
            throw new IllegalStateException(
                    "O caso só pode ser encerrado após o registro de uma conclusão ou encaminhamento final");
        }
        this.status = StatusCaso.ENCERRADO;
        return new CasoEncerradoEvento(this);
    }

    // RN1: reabertura de caso encerrado
    public CasoReabertoEvento reabrir() {
        if (status != StatusCaso.ENCERRADO) {
            throw new IllegalStateException("Apenas casos encerrados podem ser reabertos");
        }
        this.status = StatusCaso.ABERTO;
        this.triagem = null;
        this.responsavelId = null;
        this.atendimentos.clear();
        return new CasoReabertoEvento(this);
    }

    public static abstract class CasoEvento {
        private final Caso caso;
        protected CasoEvento(Caso caso) { this.caso = caso; }
        public Caso getCaso() { return caso; }
    }

    public static class TriagemRealizadaEvento extends CasoEvento {
        private TriagemRealizadaEvento(Caso caso) { super(caso); }
    }

    public static class AtendimentoRegistradoEvento extends CasoEvento {
        private final Atendimento atendimento;
        private AtendimentoRegistradoEvento(Caso caso, Atendimento atendimento) {
            super(caso);
            this.atendimento = atendimento;
        }
        public Atendimento getAtendimento() { return atendimento; }
    }

    public static class CasoEncerradoEvento extends CasoEvento {
        private CasoEncerradoEvento(Caso caso) { super(caso); }
    }

    public static class CasoReabertoEvento extends CasoEvento {
        private CasoReabertoEvento(Caso caso) { super(caso); }
    }
}
