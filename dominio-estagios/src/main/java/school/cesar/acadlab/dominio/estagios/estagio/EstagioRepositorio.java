package school.cesar.acadlab.dominio.estagios.estagio;

import java.util.List;
import java.util.Optional;
import school.cesar.acadlab.dominio.estagios.oportunidade.EstudanteId;

public interface EstagioRepositorio {
    EstagioId proximoEstagioId();
    void salvar(Estagio estagio);
    Optional<Estagio> buscarPorId(EstagioId id);
    List<Estagio> buscarPorEstudante(EstudanteId estudanteId);
}
