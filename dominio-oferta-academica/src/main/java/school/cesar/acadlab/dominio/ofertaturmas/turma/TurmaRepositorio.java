package school.cesar.acadlab.dominio.ofertaturmas.turma;

import java.util.List;
import school.cesar.acadlab.dominio.ofertaturmas.DisciplinaId;
import school.cesar.acadlab.dominio.ofertaturmas.PeriodoLetivoId;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorId;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaId;

public interface TurmaRepositorio {
    TurmaId proximoId();
    void salvar(Turma turma);
    Turma obter(TurmaId id);
    List<Turma> pesquisarPorPeriodoLetivo(PeriodoLetivoId periodoId);
    List<Turma> pesquisarPorProfessorEPeriodo(ProfessorId professorId, PeriodoLetivoId periodoId);
    List<Turma> pesquisarPorSalaEPeriodo(SalaId salaId, PeriodoLetivoId periodoId);
    List<Turma> pesquisarPorDisciplinaEPeriodo(DisciplinaId disciplinaId, PeriodoLetivoId periodoId);
}
