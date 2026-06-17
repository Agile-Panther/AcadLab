package school.cesar.acadlab.dominio.ofertaturmas.turma.decorator;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import school.cesar.acadlab.dominio.ofertaturmas.DisciplinaId;
import school.cesar.acadlab.dominio.ofertaturmas.PeriodoLetivoId;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorId;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.HorarioAula;
import school.cesar.acadlab.dominio.ofertaturmas.turma.ModalidadeTurma;
import school.cesar.acadlab.dominio.ofertaturmas.turma.StatusTurma;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaId;

public abstract class TurmaDecorador implements TurmaOferecida {
    protected final TurmaOferecida turma;

    protected TurmaDecorador(TurmaOferecida turma) {
        notNull(turma, "A turma decorada não pode ser nula");
        this.turma = turma;
    }

    @Override public TurmaId getId() { return turma.getId(); }
    @Override public PeriodoLetivoId getPeriodoLetivoId() { return turma.getPeriodoLetivoId(); }
    @Override public DisciplinaId getDisciplinaId() { return turma.getDisciplinaId(); }
    @Override public ProfessorId getProfessorId() { return turma.getProfessorId(); }
    @Override public SalaId getSalaId() { return turma.getSalaId(); }
    @Override public ModalidadeTurma getModalidade() { return turma.getModalidade(); }
    @Override public int getCapacidade() { return turma.getCapacidade(); }
    @Override public StatusTurma getStatus() { return turma.getStatus(); }
    @Override public List<HorarioAula> getHorarios() { return turma.getHorarios(); }
}
