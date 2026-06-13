package school.cesar.acadlab.aplicacao.gestaopedagogica;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;

public class DiarioTurmaServicoAplicacao {
    private final DiarioTurmaRepositorioAplicacao repositorio;

    public DiarioTurmaServicoAplicacao(DiarioTurmaRepositorioAplicacao repositorio) {
        notNull(repositorio, "repositório obrigatório");
        this.repositorio = repositorio;
    }

    public List<DiarioTurmaResumo> pesquisarPorTurma(int turmaId) {
        return repositorio.pesquisarPorTurma(turmaId);
    }
}
