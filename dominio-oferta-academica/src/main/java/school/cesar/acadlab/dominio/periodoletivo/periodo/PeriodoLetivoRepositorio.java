package school.cesar.acadlab.dominio.periodoletivo.periodo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.periodoletivo.StatusPeriodoLetivo;
import school.cesar.acadlab.dominio.periodoletivo.curso.CursoId;

public interface PeriodoLetivoRepositorio {
    PeriodoLetivoId proximoId();
    void salvar(PeriodoLetivo periodoLetivo);
    PeriodoLetivo obter(PeriodoLetivoId id);
    List<PeriodoLetivo> pesquisarPorCurso(CursoId cursoId);
    Optional<PeriodoLetivo> pesquisarPorCursoEStatus(CursoId cursoId, StatusPeriodoLetivo status);
    boolean existeSobreposicao(CursoId cursoId, LocalDate inicio, LocalDate fim);
    boolean existeSobreposicaoExcluindo(CursoId cursoId, LocalDate inicio, LocalDate fim, PeriodoLetivoId excluindo);
}
