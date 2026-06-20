package school.cesar.acadlab.dominio.estagios.candidatura;

import school.cesar.acadlab.dominio.estagios.oportunidade.EstudanteId;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeId;
import static org.apache.commons.lang3.Validate.notNull;

public class Candidatura {
    private final CandidaturaId id;
    private final OportunidadeId oportunidadeId;
    private final EstudanteId estudanteId;
    private StatusCandidatura status;

    public Candidatura(CandidaturaId id, OportunidadeId oportunidadeId, EstudanteId estudanteId) {
        this.id = notNull(id, "Id da candidatura obrigatório");
        this.oportunidadeId = notNull(oportunidadeId, "Id da oportunidade obrigatório");
        this.estudanteId = notNull(estudanteId, "Id do estudante obrigatório");
        this.status = StatusCandidatura.EM_ANALISE;
    }

    public CandidaturaCanceladaEvento cancelar() {
        if (status != StatusCandidatura.EM_ANALISE) {
            throw new IllegalStateException("candidatura não pode ser cancelada pois não está em análise");
        }
        this.status = StatusCandidatura.CANCELADA;
        return new CandidaturaCanceladaEvento(this);
    }

    public CandidaturaDeferidaEvento deferir() {
        if (status != StatusCandidatura.EM_ANALISE) {
            throw new IllegalStateException("candidatura precisa estar em análise para ser deferida");
        }
        this.status = StatusCandidatura.DEFERIDA;
        return new CandidaturaDeferidaEvento(this);
    }

    public CandidaturaIndeferidaEvento indeferir() {
        if (status != StatusCandidatura.EM_ANALISE) {
            throw new IllegalStateException("candidatura precisa estar em análise para ser indeferida");
        }
        this.status = StatusCandidatura.INDEFERIDA;
        return new CandidaturaIndeferidaEvento(this);
    }

    public CandidaturaEncaminhadaEvento encaminhar() {
        if (status != StatusCandidatura.DEFERIDA) {
            throw new IllegalStateException("candidatura precisa estar deferida para gerar encaminhamento");
        }
        this.status = StatusCandidatura.ENCAMINHADA;
        return new CandidaturaEncaminhadaEvento(this);
    }

    public static abstract class CandidaturaEvento {
        private final Candidatura candidatura;
        protected CandidaturaEvento(Candidatura candidatura) { this.candidatura = candidatura; }
        public Candidatura getCandidatura() { return candidatura; }
    }
    public static class CandidaturaCanceladaEvento extends CandidaturaEvento {
        private CandidaturaCanceladaEvento(Candidatura candidatura) { super(candidatura); }
    }
    public static class CandidaturaDeferidaEvento extends CandidaturaEvento {
        private CandidaturaDeferidaEvento(Candidatura candidatura) { super(candidatura); }
    }
    public static class CandidaturaIndeferidaEvento extends CandidaturaEvento {
        private CandidaturaIndeferidaEvento(Candidatura candidatura) { super(candidatura); }
    }
    public static class CandidaturaEncaminhadaEvento extends CandidaturaEvento {
        private CandidaturaEncaminhadaEvento(Candidatura candidatura) { super(candidatura); }
    }

    public static Candidatura reconstituir(CandidaturaId id, OportunidadeId oportunidadeId,
                                            EstudanteId estudanteId, StatusCandidatura status) {
        var c = new Candidatura(id, oportunidadeId, estudanteId);
        c.status = status;
        return c;
    }

    public CandidaturaId getId() { return id; }
    public OportunidadeId getOportunidadeId() { return oportunidadeId; }
    public EstudanteId getEstudanteId() { return estudanteId; }
    public StatusCandidatura getStatus() { return status; }
}
