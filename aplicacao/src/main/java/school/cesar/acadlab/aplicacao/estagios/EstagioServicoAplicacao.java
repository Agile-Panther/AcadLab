package school.cesar.acadlab.aplicacao.estagios;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import java.util.Optional;

public class EstagioServicoAplicacao {

    private final EstagioRepositorioAplicacao repositorio;

    public EstagioServicoAplicacao(EstagioRepositorioAplicacao repositorio) {
        notNull(repositorio, "Repositório obrigatório");
        this.repositorio = repositorio;
    }

    public Optional<EstagioResumo> buscarPorId(int id) {
        return repositorio.buscarPorId(id);
    }

    public List<EstagioResumo> buscarPorEstudante(int estudanteId) {
        return repositorio.buscarPorEstudante(estudanteId);
    }
}
