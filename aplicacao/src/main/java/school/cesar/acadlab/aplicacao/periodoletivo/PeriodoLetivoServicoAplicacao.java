package school.cesar.acadlab.aplicacao.periodoletivo;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;

public class PeriodoLetivoServicoAplicacao {
    private final PeriodoLetivoRepositorioAplicacao repositorio;

    public PeriodoLetivoServicoAplicacao(PeriodoLetivoRepositorioAplicacao repositorio) {
        notNull(repositorio, "repositório obrigatório");
        this.repositorio = repositorio;
    }

    public List<PeriodoLetivoResumo> pesquisarPorCurso(int cursoId) {
        return repositorio.pesquisarPorCurso(cursoId);
    }
}
