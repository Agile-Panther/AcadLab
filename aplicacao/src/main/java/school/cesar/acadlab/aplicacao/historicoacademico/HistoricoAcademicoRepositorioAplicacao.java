package school.cesar.acadlab.aplicacao.historicoacademico;

import java.util.List;
import java.util.Optional;

public interface HistoricoAcademicoRepositorioAplicacao {
    Optional<HistoricoAcademicoResumo> buscarPorId(int id);
    Optional<HistoricoAcademicoResumo> buscarPorEstudante(int estudanteId);
    List<HistoricoAcademicoResumo> buscarTodos();
}
