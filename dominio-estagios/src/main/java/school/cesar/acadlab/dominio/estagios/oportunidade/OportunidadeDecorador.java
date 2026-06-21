package school.cesar.acadlab.dominio.estagios.oportunidade;

import static org.apache.commons.lang3.Validate.notNull;

public abstract class OportunidadeDecorador implements OportunidadeBase {
    protected final OportunidadeBase oportunidade;

    protected OportunidadeDecorador(OportunidadeBase oportunidade) {
        this.oportunidade = notNull(oportunidade, "Oportunidade base não pode ser nula");
    }

    @Override public OportunidadeId getId() { return oportunidade.getId(); }
    @Override public EmpresaId getEmpresaId() { return oportunidade.getEmpresaId(); }
    @Override public String getDescricao() { return oportunidade.getDescricao(); }
    @Override public int getCargaHorariaTotal() { return oportunidade.getCargaHorariaTotal(); }
    @Override public StatusOportunidade getStatus() { return oportunidade.getStatus(); }
    @Override public Oportunidade.OportunidadePublicadaEvento publicar(SetorEstagiosId setorId) { return oportunidade.publicar(setorId); }
    @Override public Oportunidade.OportunidadeEncerradaEvento encerrar(MotivoEncerramento motivo) { return oportunidade.encerrar(motivo); }
    @Override public Oportunidade.CriteriosDefinidosEvento definirCriterios(SetorEstagiosId setorId, CriterioElegibilidade criterio) { return oportunidade.definirCriterios(setorId, criterio); }
    @Override public void validarCandidatura(EstudanteId estudanteId) { oportunidade.validarCandidatura(estudanteId); }
}
