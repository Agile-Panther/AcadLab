package school.cesar.acadlab.dominio.ofertaturmas;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.*;
import school.cesar.acadlab.dominio.ofertaturmas.professor.Professor;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorId;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorRepositorio;
import school.cesar.acadlab.dominio.ofertaturmas.sala.Sala;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaId;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaRepositorio;
import school.cesar.acadlab.dominio.ofertaturmas.turma.Turma;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaRepositorio;

public class Repositorio implements TurmaRepositorio, SalaRepositorio, ProfessorRepositorio {

    /*-----------------------------------------------------------------------*/
    private int proximoTurmaIdSeq = 1;
    private final Map<TurmaId, Turma> turmas = new HashMap<>();

    @Override
    public TurmaId proximoId() { return new TurmaId(proximoTurmaIdSeq++); }

    @Override
    public void salvar(Turma turma) {
        notNull(turma, "A turma não pode ser nula");
        turmas.put(turma.getId(), turma);
    }

    @Override
    public Turma obter(TurmaId id) {
        notNull(id, "O id da turma não pode ser nulo");
        return Optional.ofNullable(turmas.get(id)).get();
    }

    @Override
    public List<Turma> pesquisarPorPeriodoLetivo(PeriodoLetivoId periodoId) {
        var resultado = new ArrayList<Turma>();
        for (var t : turmas.values()) {
            if (t.getPeriodoLetivoId().equals(periodoId)) resultado.add(t);
        }
        return resultado;
    }

    @Override
    public List<Turma> pesquisarPorProfessorEPeriodo(professor.ProfessorId professorId, PeriodoLetivoId periodoId) {
        var resultado = new ArrayList<Turma>();
        for (var t : turmas.values()) {
            if (t.getPeriodoLetivoId().equals(periodoId) && professorId.equals(t.getProfessorId()))
                resultado.add(t);
        }
        return resultado;
    }

    @Override
    public List<Turma> pesquisarPorSalaEPeriodo(sala.SalaId salaId, PeriodoLetivoId periodoId) {
        var resultado = new ArrayList<Turma>();
        for (var t : turmas.values()) {
            if (t.getPeriodoLetivoId().equals(periodoId) && salaId.equals(t.getSalaId()))
                resultado.add(t);
        }
        return resultado;
    }

    @Override
    public List<Turma> pesquisarPorDisciplinaEPeriodo(DisciplinaId disciplinaId, PeriodoLetivoId periodoId) {
        var resultado = new ArrayList<Turma>();
        for (var t : turmas.values()) {
            if (t.getPeriodoLetivoId().equals(periodoId) && disciplinaId.equals(t.getDisciplinaId()))
                resultado.add(t);
        }
        return resultado;
    }
    /*-----------------------------------------------------------------------*/

    /*-----------------------------------------------------------------------*/
    private int proximaSalaIdSeq = 1;
    private final Map<SalaId, Sala> salas = new HashMap<>();

    @Override
    public SalaId proximoId() { return new SalaId(proximaSalaIdSeq++); }

    @Override
    public void salvar(Sala sala) {
        notNull(sala, "A sala não pode ser nula");
        salas.put(sala.getId(), sala);
    }

    @Override
    public Sala obter(SalaId id) {
        notNull(id, "O id da sala não pode ser nulo");
        return Optional.ofNullable(salas.get(id)).get();
    }

    @Override
    public List<Sala> pesquisarAtivas() {
        var resultado = new ArrayList<Sala>();
        for (var s : salas.values()) {
            if (s.isAtiva()) resultado.add(s);
        }
        return resultado;
    }
    /*-----------------------------------------------------------------------*/

    /*-----------------------------------------------------------------------*/
    private int proximoProfessorIdSeq = 1;
    private final Map<ProfessorId, Professor> professores = new HashMap<>();

    @Override
    public ProfessorId proximoId() { return new ProfessorId(proximoProfessorIdSeq++); }

    @Override
    public void salvar(Professor professor) {
        notNull(professor, "O professor não pode ser nulo");
        professores.put(professor.getId(), professor);
    }

    @Override
    public Professor obter(ProfessorId id) {
        notNull(id, "O id do professor não pode ser nulo");
        return Optional.ofNullable(professores.get(id)).get();
    }

    @Override
    public List<Professor> pesquisarAtivos() {
        var resultado = new ArrayList<Professor>();
        for (var p : professores.values()) {
            if (p.isAtivo()) resultado.add(p);
        }
        return resultado;
    }
    /*-----------------------------------------------------------------------*/
}
