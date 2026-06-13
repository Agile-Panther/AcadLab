package school.cesar.acadlab.aplicacao.periodoletivo;

import java.util.List;

public interface PeriodoLetivoRepositorioAplicacao {
    List<PeriodoLetivoResumo> pesquisarPorCurso(int cursoId);
}
