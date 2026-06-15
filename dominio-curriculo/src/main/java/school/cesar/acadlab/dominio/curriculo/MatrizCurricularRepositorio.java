package school.cesar.acadlab.dominio.curriculo;

import java.util.List;
import java.util.Optional;

public interface MatrizCurricularRepositorio {
    void salvar(MatrizCurricular matriz);
    Optional<MatrizCurricular> buscarPorId(MatrizCurricularId id);
    List<MatrizCurricular> buscarPorCurso(CursoId cursoId);
    MatrizCurricularId proximaMatrizId();
}
