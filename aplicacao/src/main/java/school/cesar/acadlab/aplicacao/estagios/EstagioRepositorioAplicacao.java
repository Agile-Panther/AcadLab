package school.cesar.acadlab.aplicacao.estagios;

import java.util.List;
import java.util.Optional;

public interface EstagioRepositorioAplicacao {
    Optional<EstagioResumo> buscarPorId(int id);
    List<EstagioResumo> buscarPorEstudante(int estudanteId);
}
