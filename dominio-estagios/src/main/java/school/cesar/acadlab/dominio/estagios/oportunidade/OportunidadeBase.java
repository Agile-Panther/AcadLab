package school.cesar.acadlab.dominio.estagios.oportunidade;

public interface OportunidadeBase {
    OportunidadeId getId();
    EmpresaId getEmpresaId();
    String getDescricao();
    int getCargaHorariaTotal();
    StatusOportunidade getStatus();
    Oportunidade.OportunidadePublicadaEvento publicar(SetorEstagiosId setorId);
    Oportunidade.OportunidadeEncerradaEvento encerrar(MotivoEncerramento motivo);
    Oportunidade.CriteriosDefinidosEvento definirCriterios(SetorEstagiosId setorId, CriterioElegibilidade criterio);
    void validarCandidatura(EstudanteId estudanteId);
}
