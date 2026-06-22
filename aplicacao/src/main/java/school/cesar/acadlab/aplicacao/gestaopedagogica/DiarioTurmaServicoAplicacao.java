package school.cesar.acadlab.aplicacao.gestaopedagogica;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import java.util.Optional;

public class DiarioTurmaServicoAplicacao {
    private final DiarioTurmaRepositorioAplicacao repositorio;

    public DiarioTurmaServicoAplicacao(DiarioTurmaRepositorioAplicacao repositorio) {
        notNull(repositorio, "repositório obrigatório");
        this.repositorio = repositorio;
    }

    public List<DiarioTurmaResumo> pesquisarPorTurma(int turmaId) {
        return repositorio.pesquisarPorTurma(turmaId);
    }

    public List<DiarioTurmaResumo> pesquisarPorProfessor(int professorId) {
        return repositorio.pesquisarPorProfessor(professorId);
    }

    public List<DiarioTurmaResumo> pesquisarTodos() {
        return repositorio.pesquisarTodos();
    }

    public Optional<DiarioTurmaDetalhadoResumo> buscarDetalhado(int id) {
        return repositorio.buscarDetalhado(id);
    }
}
