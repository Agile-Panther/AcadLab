package school.cesar.acadlab.aplicacao.matricula;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

public class MatriculaServicoAplicacao {
    private final MatriculaRepositorioAplicacao repositorio;

    public MatriculaServicoAplicacao(MatriculaRepositorioAplicacao repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public List<MatriculaResumo> buscarPorEstudante(int estudanteId) {
        return repositorio.buscarPorEstudante(estudanteId);
    }

    public Optional<MatriculaResumo> buscarPorId(int id) {
        return repositorio.buscarPorId(id);
    }
}
