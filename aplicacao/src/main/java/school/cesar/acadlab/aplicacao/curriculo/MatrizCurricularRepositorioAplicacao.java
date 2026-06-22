package school.cesar.acadlab.aplicacao.curriculo;

import java.util.List;
import java.util.Optional;

public interface MatrizCurricularRepositorioAplicacao {
    List<MatrizCurricularResumo> buscarPorCurso(int cursoId);
    Optional<MatrizCurricularResumo> buscarPorId(int id);
    Optional<MatrizCurricularDetalhe> buscarDetalhePorId(int id);
}
