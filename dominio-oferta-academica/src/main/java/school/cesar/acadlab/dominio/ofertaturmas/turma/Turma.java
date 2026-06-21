package school.cesar.acadlab.dominio.ofertaturmas.turma;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import school.cesar.acadlab.dominio.ofertaturmas.DisciplinaId;
import school.cesar.acadlab.dominio.ofertaturmas.PeriodoLetivoId;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorId;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.decorator.TurmaOferecida;

public class Turma implements TurmaOferecida {
    private final TurmaId id;
    private final PeriodoLetivoId periodoLetivoId;
    private final DisciplinaId disciplinaId;
    private ProfessorId professorId;
    private SalaId salaId;
    private final ModalidadeTurma modalidade;
    private final int capacidade;
    private final List<HorarioAula> horarios = new ArrayList<>();
    private StatusTurma status;

    // RN4: disciplina pertence à matriz ativa — verificado externamente
    // RN5: turma dentro das datas do período — verificado externamente
    public Turma(TurmaId id, PeriodoLetivoId periodoLetivoId, DisciplinaId disciplinaId,
                 ModalidadeTurma modalidade, int capacidade) {
        notNull(id, "O id não pode ser nulo");
        notNull(periodoLetivoId, "O período letivo não pode ser nulo");
        notNull(disciplinaId, "A disciplina não pode ser nula");
        notNull(modalidade, "A modalidade não pode ser nula");
        if (capacidade <= 0) throw new IllegalArgumentException("A capacidade deve ser positiva");
        this.id = id;
        this.periodoLetivoId = periodoLetivoId;
        this.disciplinaId = disciplinaId;
        this.modalidade = modalidade;
        this.capacidade = capacidade;
        this.status = StatusTurma.PLANEJADA;
    }

    // US04 - RN3: professor ativo verificado externamente; RN6: conflito de horário verificado externamente
    public ProfessorVinculadoEvento vincularProfessor(ProfessorId professorId) {
        notNull(professorId, "O professor não pode ser nulo");
        this.professorId = professorId;
        return new ProfessorVinculadoEvento(this);
    }

    // US04 - RN1: sala ativa verificada externamente; RN8: capacidade da turma não excede a sala
    public SalaVinculadaEvento vincularSala(SalaId salaId, int capacidadeSala) {
        notNull(salaId, "A sala não pode ser nula");
        if (this.capacidade > capacidadeSala) {
            throw new IllegalStateException("capacidade da sala é insuficiente para a turma");
        }
        this.salaId = salaId;
        return new SalaVinculadaEvento(this);
    }

    public HorarioAdicionadoEvento adicionarHorario(HorarioAula horario) {
        notNull(horario, "O horário não pode ser nulo");
        horarios.add(horario);
        return new HorarioAdicionadoEvento(this, horario);
    }

    // US03 - ofertar turma (pré-condições de professor, sala e horário devem estar satisfeitas)
    public TurmaOfertadaEvento ofertar() {
        if (professorId == null) throw new IllegalStateException("Professor não vinculado à turma");
        if (salaId == null) throw new IllegalStateException("Sala não vinculada à turma");
        if (horarios.isEmpty()) throw new IllegalStateException("Nenhum horário definido para a turma");
        this.status = StatusTurma.OFERTADA;
        return new TurmaOfertadaEvento(this);
    }

    // US07 - RN10: matrículas confirmadas tratadas externamente antes desta chamada
    // RN11: notificação de estudantes registrada externamente
    public TurmaCanceladaEvento cancelar() {
        if (status == StatusTurma.CANCELADA) {
            throw new IllegalStateException("A turma já está cancelada");
        }
        this.status = StatusTurma.CANCELADA;
        return new TurmaCanceladaEvento(this);
    }

    public static Turma reconstituir(TurmaId id, PeriodoLetivoId periodoLetivoId, DisciplinaId disciplinaId,
            ProfessorId professorId, SalaId salaId, ModalidadeTurma modalidade,
            int capacidade, StatusTurma status, List<HorarioAula> horarios) {
        var turma = new Turma(id, periodoLetivoId, disciplinaId, modalidade, capacidade);
        turma.professorId = professorId;
        turma.salaId = salaId;
        turma.status = status;
        turma.horarios.addAll(horarios);
        return turma;
    }

    public TurmaId getId() { return id; }
    public PeriodoLetivoId getPeriodoLetivoId() { return periodoLetivoId; }
    public DisciplinaId getDisciplinaId() { return disciplinaId; }
    public ProfessorId getProfessorId() { return professorId; }
    public SalaId getSalaId() { return salaId; }
    public ModalidadeTurma getModalidade() { return modalidade; }
    public int getCapacidade() { return capacidade; }
    public StatusTurma getStatus() { return status; }
    public List<HorarioAula> getHorarios() { return Collections.unmodifiableList(horarios); }

    public static abstract class TurmaEvento {
        private final Turma turma;
        protected TurmaEvento(Turma turma) { this.turma = turma; }
        public Turma getTurma() { return turma; }
    }

    public static class ProfessorVinculadoEvento extends TurmaEvento {
        private ProfessorVinculadoEvento(Turma turma) { super(turma); }
    }

    public static class SalaVinculadaEvento extends TurmaEvento {
        private SalaVinculadaEvento(Turma turma) { super(turma); }
    }

    public static class HorarioAdicionadoEvento extends TurmaEvento {
        private final HorarioAula horario;
        private HorarioAdicionadoEvento(Turma turma, HorarioAula horario) {
            super(turma);
            this.horario = horario;
        }
        public HorarioAula getHorario() { return horario; }
    }

    public static class TurmaOfertadaEvento extends TurmaEvento {
        private TurmaOfertadaEvento(Turma turma) { super(turma); }
    }

    public static class TurmaCanceladaEvento extends TurmaEvento {
        private TurmaCanceladaEvento(Turma turma) { super(turma); }
    }
}
