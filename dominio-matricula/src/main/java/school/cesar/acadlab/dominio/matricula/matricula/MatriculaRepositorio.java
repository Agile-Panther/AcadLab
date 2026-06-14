package school.cesar.acadlab.dominio.matricula.matricula;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepositorio {
    void salvar(Matricula matricula);
    Optional<Matricula> buscarPorId(MatriculaId id);
    List<Matricula> buscarPorEstudante(EstudanteId estudanteId);
    MatriculaId proximaMatriculaId();
}
