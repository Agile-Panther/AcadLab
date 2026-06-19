package school.cesar.acadlab.dominio.estagios.oportunidade;

public interface OportunidadeBase {
    OportunidadeId getId();
    EmpresaId getEmpresaId();
    String getDescricao();
    int getCargaHorariaTotal();
    StatusOportunidade getStatus();
    void publicar(SetorEstagiosId setorId);
    void encerrar(MotivoEncerramento motivo);
    void definirCriterios(SetorEstagiosId setorId, CriterioElegibilidade criterio);
    void validarCandidatura(EstudanteId estudanteId);
}
