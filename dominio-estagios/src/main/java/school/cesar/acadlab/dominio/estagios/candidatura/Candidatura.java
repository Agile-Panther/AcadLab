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

    public void cancelar() {
        if (status != StatusCandidatura.EM_ANALISE) {
            throw new IllegalStateException("candidatura não pode ser cancelada pois não está em análise");
        }
        this.status = StatusCandidatura.CANCELADA;
    }

    public void deferir() {
        if (status != StatusCandidatura.EM_ANALISE) {
            throw new IllegalStateException("candidatura precisa estar em análise para ser deferida");
        }
        this.status = StatusCandidatura.DEFERIDA;
    }

    public void indeferir() {
        if (status != StatusCandidatura.EM_ANALISE) {
            throw new IllegalStateException("candidatura precisa estar em análise para ser indeferida");
        }
        this.status = StatusCandidatura.INDEFERIDA;
    }

    public void encaminhar() {
        if (status != StatusCandidatura.DEFERIDA) {
            throw new IllegalStateException("candidatura precisa estar deferida para gerar encaminhamento");
        }
        this.status = StatusCandidatura.ENCAMINHADA;
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
