package school.cesar.acadlab.aplicacao.curriculo;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;
import java.util.Optional;

public class MatrizCurricularServicoAplicacao {

    private final MatrizCurricularRepositorioAplicacao repositorio;

    public MatrizCurricularServicoAplicacao(MatrizCurricularRepositorioAplicacao repositorio) {
        notNull(repositorio, "repositório obrigatório");
        this.repositorio = repositorio;
    }

    public List<MatrizCurricularResumo> buscarPorCurso(int cursoId) {
        return repositorio.buscarPorCurso(cursoId);
    }

    public Optional<MatrizCurricularResumo> buscarPorId(int id) {
        return repositorio.buscarPorId(id);
    }

    public Optional<MatrizCurricularDetalhe> buscarDetalhePorId(int id) {
        return repositorio.buscarDetalhePorId(id);
    }
}
