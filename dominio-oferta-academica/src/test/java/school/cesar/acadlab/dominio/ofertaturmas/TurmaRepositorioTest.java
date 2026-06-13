package school.cesar.acadlab.dominio.ofertaturmas;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.*;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorId;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.Turma;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaRepositorio;

public class TurmaRepositorioTest implements TurmaRepositorio {
    private int proximoSeq = 1;
    private final Map<TurmaId, Turma> turmas = new HashMap<>();

    @Override
    public TurmaId proximoId() { return new TurmaId(proximoSeq++); }

    @Override
    public void salvar(Turma turma) {
        notNull(turma, "A turma não pode ser nula");
        turmas.put(turma.getId(), turma);
    }

    @Override
    public Turma obter(TurmaId id) {
        notNull(id, "O id da turma não pode ser nulo");
        return Optional.ofNullable(turmas.get(id)).orElseThrow();
    }

    @Override
    public List<Turma> pesquisarPorPeriodoLetivo(PeriodoLetivoId periodoId) {
        var resultado = new ArrayList<Turma>();
        for (var t : turmas.values())
            if (t.getPeriodoLetivoId().equals(periodoId)) resultado.add(t);
        return resultado;
    }

    @Override
    public List<Turma> pesquisarPorProfessorEPeriodo(ProfessorId professorId, PeriodoLetivoId periodoId) {
        var resultado = new ArrayList<Turma>();
        for (var t : turmas.values())
            if (t.getPeriodoLetivoId().equals(periodoId) && professorId.equals(t.getProfessorId()))
                resultado.add(t);
        return resultado;
    }

    @Override
    public List<Turma> pesquisarPorSalaEPeriodo(SalaId salaId, PeriodoLetivoId periodoId) {
        var resultado = new ArrayList<Turma>();
        for (var t : turmas.values())
            if (t.getPeriodoLetivoId().equals(periodoId) && salaId.equals(t.getSalaId()))
                resultado.add(t);
        return resultado;
    }

    @Override
    public List<Turma> pesquisarPorDisciplinaEPeriodo(DisciplinaId disciplinaId, PeriodoLetivoId periodoId) {
        var resultado = new ArrayList<Turma>();
        for (var t : turmas.values())
            if (t.getPeriodoLetivoId().equals(periodoId) && disciplinaId.equals(t.getDisciplinaId()))
                resultado.add(t);
        return resultado;
    }
}
