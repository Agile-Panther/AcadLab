package school.cesar.acadlab.dominio.periodoletivo;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import school.cesar.acadlab.dominio.periodoletivo.curso.CursoId;
import school.cesar.acadlab.dominio.periodoletivo.periodo.PeriodoLetivo;
import school.cesar.acadlab.dominio.periodoletivo.periodo.PeriodoLetivoRepositorio;

public class ConsultaPeriodoLetivoServico {
    private final PeriodoLetivoRepositorio repositorio;

    public ConsultaPeriodoLetivoServico(PeriodoLetivoRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public List<PeriodoLetivo> listarPorCurso(CursoId cursoId) {
        notNull(cursoId, "O curso não pode ser nulo");
        return repositorio.pesquisarPorCurso(cursoId);
    }

    public PeriodoLetivo buscar(PeriodoLetivoId id) {
        notNull(id, "O id não pode ser nulo");
        return repositorio.obter(id);
    }
}
