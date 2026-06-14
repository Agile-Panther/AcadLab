package school.cesar.acadlab.aplicacao.matricula;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepositorioAplicacao {
    List<MatriculaResumo> buscarPorEstudante(int estudanteId);
    Optional<MatriculaResumo> buscarPorId(int id);
}
