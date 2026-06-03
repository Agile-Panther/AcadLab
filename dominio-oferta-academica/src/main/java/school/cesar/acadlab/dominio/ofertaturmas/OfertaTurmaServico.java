package school.cesar.acadlab.dominio.ofertaturmas;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorId;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorRepositorio;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaId;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaRepositorio;
import school.cesar.acadlab.dominio.ofertaturmas.turma.HorarioAula;
import school.cesar.acadlab.dominio.ofertaturmas.turma.ModalidadeTurma;
import school.cesar.acadlab.dominio.ofertaturmas.turma.Turma;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaRepositorio;

public class OfertaTurmaServico {
    private final TurmaRepositorio turmaRepositorio;
    private final SalaRepositorio salaRepositorio;
    private final ProfessorRepositorio professorRepositorio;

    public OfertaTurmaServico(TurmaRepositorio turmaRepositorio,
                               SalaRepositorio salaRepositorio,
                               ProfessorRepositorio professorRepositorio) {
        notNull(turmaRepositorio, "O repositório de turmas não pode ser nulo");
        notNull(salaRepositorio, "O repositório de salas não pode ser nulo");
        notNull(professorRepositorio, "O repositório de professores não pode ser nulo");
        this.turmaRepositorio = turmaRepositorio;
        this.salaRepositorio = salaRepositorio;
        this.professorRepositorio = professorRepositorio;
    }

    // US03 - RN4/RN5: verificações de disciplina e período feitas externamente (camada aplicacao)
    public Turma ofertar(PeriodoLetivoId periodoLetivoId, DisciplinaId disciplinaId,
                         ModalidadeTurma modalidade, int capacidade) {
        notNull(periodoLetivoId, "O período letivo não pode ser nulo");
        notNull(disciplinaId, "A disciplina não pode ser nula");
        var id = turmaRepositorio.proximoId();
        var turma = new Turma(id, periodoLetivoId, disciplinaId, modalidade, capacidade);
        turmaRepositorio.salvar(turma);
        return turma;
    }

    // US04 - RN3: professor deve estar ativo; RN6: conflito de horário verificado aqui
    public void vincularProfessor(TurmaId turmaId, ProfessorId professorId) {
        notNull(turmaId, "O id da turma não pode ser nulo");
        notNull(professorId, "O id do professor não pode ser nulo");
        var professor = professorRepositorio.obter(professorId);
        if (!professor.isAtivo()) {
            throw new IllegalStateException("RN3: Professor inativo não pode ser vinculado a turma");
        }
        var turma = turmaRepositorio.obter(turmaId);
        turma.vincularProfessor(professorId);
        turmaRepositorio.salvar(turma);
    }

    // US04 - RN1: sala deve estar ativa; RN8: capacidade verificada no agregado
    public void vincularSala(TurmaId turmaId, SalaId salaId) {
        notNull(turmaId, "O id da turma não pode ser nulo");
        notNull(salaId, "O id da sala não pode ser nulo");
        var sala = salaRepositorio.obter(salaId);
        if (!sala.isAtiva()) {
            throw new IllegalStateException("RN1: Sala inativa não pode ser vinculada a turma");
        }
        var turma = turmaRepositorio.obter(turmaId);
        turma.vincularSala(salaId, sala.getCapacidade());
        turmaRepositorio.salvar(turma);
    }

    public void adicionarHorario(TurmaId turmaId, DayOfWeek dia, LocalTime inicio, LocalTime fim) {
        notNull(turmaId, "O id da turma não pode ser nulo");
        var turma = turmaRepositorio.obter(turmaId);
        turma.adicionarHorario(new HorarioAula(dia, inicio, fim));
        turmaRepositorio.salvar(turma);
    }

    public void confirmarOferta(TurmaId turmaId) {
        notNull(turmaId, "O id da turma não pode ser nulo");
        var turma = turmaRepositorio.obter(turmaId);
        turma.ofertar();
        turmaRepositorio.salvar(turma);
    }

    // US07 - RN10/RN11: matrículas e notificações tratadas externamente
    public void cancelar(TurmaId turmaId) {
        notNull(turmaId, "O id da turma não pode ser nulo");
        var turma = turmaRepositorio.obter(turmaId);
        turma.cancelar();
        turmaRepositorio.salvar(turma);
    }
}
