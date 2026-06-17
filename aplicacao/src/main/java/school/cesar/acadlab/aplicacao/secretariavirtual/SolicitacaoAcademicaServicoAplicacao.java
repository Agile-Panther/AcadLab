package school.cesar.acadlab.aplicacao.secretariavirtual;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import java.util.Optional;

public class SolicitacaoAcademicaServicoAplicacao {

    private final SolicitacaoAcademicaRepositorioAplicacao repositorio;

    public SolicitacaoAcademicaServicoAplicacao(SolicitacaoAcademicaRepositorioAplicacao repositorio) {
        notNull(repositorio, "repositório obrigatório");
        this.repositorio = repositorio;
    }

    public List<SolicitacaoAcademicaResumo> buscarPorEstudante(int estudanteId) {
        return repositorio.buscarPorEstudante(estudanteId);
    }

    public Optional<SolicitacaoAcademicaResumo> buscarPorId(int id) {
        return repositorio.buscarPorId(id);
    }

    public List<SolicitacaoAcademicaResumo> buscarPendentesDeAnalise() {
        return repositorio.buscarPorStatus("PENDENTE_ANALISE");
    }
}
