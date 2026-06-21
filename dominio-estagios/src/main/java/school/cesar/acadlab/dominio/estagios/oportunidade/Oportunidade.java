package school.cesar.acadlab.dominio.estagios.oportunidade;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class Oportunidade implements OportunidadeBase {
    private final OportunidadeId id;
    private final EmpresaId empresaId;
    private final String descricao;
    private final int cargaHorariaTotal;
    private StatusOportunidade status;
    private CriterioElegibilidade criterioElegibilidade;

    public Oportunidade(OportunidadeId id, EmpresaId empresaId, String descricao, int cargaHorariaTotal) {
        notBlank(descricao, "Descrição obrigatória");
        isTrue(cargaHorariaTotal > 0, "Carga horária deve ser positiva");
        this.id = notNull(id, "Id obrigatório");
        this.empresaId = notNull(empresaId, "Id da empresa obrigatório");
        this.descricao = descricao;
        this.cargaHorariaTotal = cargaHorariaTotal;
        this.status = StatusOportunidade.CADASTRADA;
    }

    @Override
    public OportunidadePublicadaEvento publicar(SetorEstagiosId setorId) {
        notNull(setorId, "Id do setor de estágios obrigatório para publicação");
        if (status != StatusOportunidade.CADASTRADA) {
            throw new IllegalStateException("publicação só pode ser realizada em oportunidades cadastradas");
        }
        this.status = StatusOportunidade.PUBLICADA;
        return new OportunidadePublicadaEvento(this);
    }

    @Override
    public OportunidadeEncerradaEvento encerrar(MotivoEncerramento motivo) {
        notNull(motivo, "Motivo de encerramento obrigatório");
        if (status != StatusOportunidade.PUBLICADA) {
            throw new IllegalStateException("somente oportunidades publicadas podem ser encerradas");
        }
        this.status = StatusOportunidade.ENCERRADA;
        return new OportunidadeEncerradaEvento(this);
    }

    @Override
    public CriteriosDefinidosEvento definirCriterios(SetorEstagiosId setorId, CriterioElegibilidade criterio) {
        notNull(setorId, "Id do setor de estágios obrigatório");
        notNull(criterio, "Critério de elegibilidade obrigatório");
        if (status == StatusOportunidade.PUBLICADA || status == StatusOportunidade.ENCERRADA) {
            throw new IllegalStateException("critérios não podem ser alterados após a publicação");
        }
        this.criterioElegibilidade = criterio;
        return new CriteriosDefinidosEvento(this);
    }

    public static abstract class OportunidadeEvento {
        private final Oportunidade oportunidade;
        protected OportunidadeEvento(Oportunidade oportunidade) { this.oportunidade = oportunidade; }
        public Oportunidade getOportunidade() { return oportunidade; }
    }
    public static class OportunidadePublicadaEvento extends OportunidadeEvento {
        private OportunidadePublicadaEvento(Oportunidade oportunidade) { super(oportunidade); }
    }
    public static class OportunidadeEncerradaEvento extends OportunidadeEvento {
        private OportunidadeEncerradaEvento(Oportunidade oportunidade) { super(oportunidade); }
    }
    public static class CriteriosDefinidosEvento extends OportunidadeEvento {
        private CriteriosDefinidosEvento(Oportunidade oportunidade) { super(oportunidade); }
    }

    @Override
    public void validarCandidatura(EstudanteId estudanteId) {
        if (status != StatusOportunidade.PUBLICADA) {
            throw new IllegalStateException("oportunidade não está disponível para candidaturas");
        }
    }

    public static Oportunidade reconstituir(OportunidadeId id, EmpresaId empresaId,
                                             String descricao, int cargaHorariaTotal,
                                             StatusOportunidade status,
                                             CriterioElegibilidade criterioElegibilidade) {
        var o = new Oportunidade(id, empresaId, descricao, cargaHorariaTotal);
        o.status = status;
        o.criterioElegibilidade = criterioElegibilidade;
        return o;
    }

    @Override public OportunidadeId getId() { return id; }
    @Override public EmpresaId getEmpresaId() { return empresaId; }
    @Override public String getDescricao() { return descricao; }
    @Override public int getCargaHorariaTotal() { return cargaHorariaTotal; }
    @Override public StatusOportunidade getStatus() { return status; }
    public CriterioElegibilidade getCriterioElegibilidade() { return criterioElegibilidade; }
}
