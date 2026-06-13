package school.cesar.acadlab.aplicacao.gestaopedagogica;

import java.util.List;

public interface DiarioTurmaRepositorioAplicacao {
    List<DiarioTurmaResumo> pesquisarPorTurma(int turmaId);
}
