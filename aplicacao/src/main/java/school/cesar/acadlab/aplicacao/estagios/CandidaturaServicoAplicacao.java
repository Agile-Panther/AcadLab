package school.cesar.acadlab.aplicacao.estagios;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;

public class CandidaturaServicoAplicacao {

    private final CandidaturaRepositorioAplicacao repositorio;

    public CandidaturaServicoAplicacao(CandidaturaRepositorioAplicacao repositorio) {
        notNull(repositorio, "Repositório obrigatório");
        this.repositorio = repositorio;
    }

    public List<CandidaturaResumo> listarTodas() {
        return repositorio.listarTodas();
    }

    public List<CandidaturaResumo> buscarPorEstudante(int estudanteId) {
        return repositorio.buscarPorEstudante(estudanteId);
    }
}
