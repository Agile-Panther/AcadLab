package school.cesar.acadlab.aplicacao.historicoacademico;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import java.util.Optional;

public class HistoricoAcademicoServicoAplicacao {

    private final HistoricoAcademicoRepositorioAplicacao repositorio;

    public HistoricoAcademicoServicoAplicacao(HistoricoAcademicoRepositorioAplicacao repositorio) {
        notNull(repositorio, "Repositório obrigatório");
        this.repositorio = repositorio;
    }

    public Optional<HistoricoAcademicoResumo> buscarPorId(int id) {
        return repositorio.buscarPorId(id);
    }

    public Optional<HistoricoAcademicoResumo> buscarPorEstudante(int estudanteId) {
        return repositorio.buscarPorEstudante(estudanteId);
    }

    public List<HistoricoAcademicoResumo> buscarTodos() {
        return repositorio.buscarTodos();
    }
}
