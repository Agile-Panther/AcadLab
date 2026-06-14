package school.cesar.acadlab.dominio.estagios.oportunidade;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;

public class Oportunidade {
    private final OportunidadeId id;
    private final EmpresaId empresaId;
    private final String descricao;
    private final int cargaHorariaTotal;
    private StatusOportunidade status;
    private EstudanteId candidato;

    public Oportunidade(OportunidadeId id, EmpresaId empresaId, String descricao, int cargaHorariaTotal) {
        notBlank(descricao, "Descrição obrigatória");
        isTrue(cargaHorariaTotal > 0, "Carga horária deve ser positiva");
        this.id = id;
        this.empresaId = empresaId;
        this.descricao = descricao;
        this.cargaHorariaTotal = cargaHorariaTotal;
        this.status = StatusOportunidade.ABERTA;
    }

    public void candidatar(EstudanteId estudanteId) {
        if (status != StatusOportunidade.ABERTA) {
            throw new IllegalStateException("RN-1: Candidatura só permitida em oportunidades abertas");
        }
        if (candidato != null) {
            throw new IllegalStateException("RN-2: Oportunidade já possui candidato");
        }
        this.candidato = estudanteId;
    }

    public void encaminhar(CoordenadorId coordenadorId) {
        if (status != StatusOportunidade.ABERTA) {
            throw new IllegalStateException("RN-3: Encaminhamento só permitido quando a oportunidade está aberta");
        }
        if (candidato == null) {
            throw new IllegalStateException("RN-4: Oportunidade precisa de candidato antes de ser encaminhada");
        }
        this.status = StatusOportunidade.ENCAMINHADA;
    }

    public void confirmar(EmpresaId empresaIdConfirmador) {
        if (status != StatusOportunidade.ENCAMINHADA) {
            throw new IllegalStateException("RN-5: Confirmação só permitida após encaminhamento");
        }
        this.status = StatusOportunidade.CONFIRMADA;
    }

    public void recusar(EmpresaId empresaIdRecusador) {
        if (status != StatusOportunidade.ENCAMINHADA) {
            throw new IllegalStateException("RN-6: Recusa só permitida após encaminhamento");
        }
        this.status = StatusOportunidade.RECUSADA;
    }

    public static Oportunidade reconstituir(OportunidadeId id, EmpresaId empresaId,
                                            String descricao, int cargaHorariaTotal,
                                            StatusOportunidade status, EstudanteId candidato) {
        var o = new Oportunidade(id, empresaId, descricao, cargaHorariaTotal);
        o.status = status;
        o.candidato = candidato;
        return o;
    }

    public OportunidadeId getId() { return id; }
    public EmpresaId getEmpresaId() { return empresaId; }
    public String getDescricao() { return descricao; }
    public int getCargaHorariaTotal() { return cargaHorariaTotal; }
    public StatusOportunidade getStatus() { return status; }
    public EstudanteId getCandidato() { return candidato; }
}
