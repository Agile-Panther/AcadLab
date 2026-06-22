package school.cesar.acadlab.dominio.ofertaturmas.turma.decorator;

import java.util.List;
import school.cesar.acadlab.dominio.ofertaturmas.DisciplinaId;
import school.cesar.acadlab.dominio.ofertaturmas.PeriodoLetivoId;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorId;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.HorarioAula;
import school.cesar.acadlab.dominio.ofertaturmas.turma.ModalidadeTurma;
import school.cesar.acadlab.dominio.ofertaturmas.turma.StatusTurma;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaId;

public interface TurmaOferecida {
    TurmaId getId();
    PeriodoLetivoId getPeriodoLetivoId();
    DisciplinaId getDisciplinaId();
    ProfessorId getProfessorId();
    SalaId getSalaId();
    ModalidadeTurma getModalidade();
    int getCapacidade();
    boolean isListaEsperaHabilitada();
    StatusTurma getStatus();
    List<HorarioAula> getHorarios();
}
