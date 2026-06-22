package school.cesar.acadlab.aplicacao.gestaopedagogica;

import java.util.List;
import java.util.Optional;

public interface DiarioTurmaRepositorioAplicacao {
    List<DiarioTurmaResumo> pesquisarPorTurma(int turmaId);
    List<DiarioTurmaResumo> pesquisarPorProfessor(int professorId);
    List<DiarioTurmaResumo> pesquisarTodos();
    Optional<DiarioTurmaDetalhadoResumo> buscarDetalhado(int id);
}
