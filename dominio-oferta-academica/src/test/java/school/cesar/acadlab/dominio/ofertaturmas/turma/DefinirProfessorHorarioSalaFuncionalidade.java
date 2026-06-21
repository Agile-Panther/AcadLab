package school.cesar.acadlab.dominio.ofertaturmas.turma;

import static org.junit.jupiter.api.Assertions.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.ofertaturmas.DisciplinaId;
import school.cesar.acadlab.dominio.ofertaturmas.OfertaTurmasFuncionalidade;
import school.cesar.acadlab.dominio.ofertaturmas.PeriodoLetivoId;
import school.cesar.acadlab.dominio.ofertaturmas.professor.Professor;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorId;
import school.cesar.acadlab.dominio.ofertaturmas.sala.Sala;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaId;

public class DefinirProfessorHorarioSalaFuncionalidade extends OfertaTurmasFuncionalidade {

    private final PeriodoLetivoId periodoId = new PeriodoLetivoId(1);
    private final DisciplinaId disciplinaId = new DisciplinaId(1);

    private Turma turma;
    private ProfessorId professorId;
    private SalaId salaId;
    private RuntimeException excecaoLocal;

    @Dado("uma turma planejada com horário segunda 08h às 10h no período 1")
    public void turma_planejada_com_horario() {
        turma = ofertaTurmaServico.ofertar(periodoId, disciplinaId, ModalidadeTurma.PRESENCIAL, 30);
        ofertaTurmaServico.adicionarHorario(turma.getId(),
                DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));
        turma = turmaRepositorio.obter(turma.getId());
    }

    @Dado("um professor sem turmas no mesmo período")
    public void professor_sem_turmas() {
        professorId = professorRepositorio.proximoId();
        professorRepositorio.salvar(new Professor(professorId, "Prof. Sem Conflito"));
    }

    @Quando("a coordenação adiciona o horário e vincula o professor à turma")
    public void vincular_professor_sem_conflito() {
        try {
            ofertaTurmaServico.vincularProfessor(turma.getId(), professorId);
        } catch (RuntimeException e) {
            excecaoLocal = e;
        }
    }

    @Entao("o professor é vinculado com sucesso")
    public void professor_vinculado_com_sucesso() {
        assertNull(excecaoLocal, "Não deveria ter lançado exceção");
        var turmaAtualizada = turmaRepositorio.obter(turma.getId());
        assertEquals(professorId, turmaAtualizada.getProfessorId());
    }

    @Dado("o professor já tem turma com horário segunda 09h às 11h no mesmo período")
    public void professor_com_turma_conflitante() {
        professorId = professorRepositorio.proximoId();
        professorRepositorio.salvar(new Professor(professorId, "Prof. Ocupado"));

        var outraId = turmaRepositorio.proximoId();
        var outra = new Turma(outraId, periodoId, new DisciplinaId(2), ModalidadeTurma.PRESENCIAL, 30);
        outra.adicionarHorario(new HorarioAula(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0)));
        outra.vincularProfessor(professorId);
        turmaRepositorio.salvar(outra);
    }

    @Quando("a coordenação tenta vincular o professor com conflito")
    public void tentar_vincular_professor_com_conflito() {
        try {
            ofertaTurmaServico.vincularProfessor(turma.getId(), professorId);
        } catch (RuntimeException e) {
            excecaoLocal = e;
        }
    }

    @Entao("o sistema deve rejeitar informando conflito de horário do professor")
    public void rejeitar_conflito_professor() {
        assertNotNull(excecaoLocal);
        assertTrue(excecaoLocal.getMessage().contains("conflito de horário do professor"),
                "Mensagem obtida: " + excecaoLocal.getMessage());
    }

    @Dado("a sala já está alocada em turma com horário segunda 09h às 11h no mesmo período")
    public void sala_com_turma_conflitante() {
        salaId = salaRepositorio.proximoId();
        salaRepositorio.salvar(new Sala(salaId, "Sala Ocupada", 40));

        var outraId = turmaRepositorio.proximoId();
        var outra = new Turma(outraId, periodoId, new DisciplinaId(2), ModalidadeTurma.PRESENCIAL, 30);
        outra.adicionarHorario(new HorarioAula(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0)));
        outra.vincularSala(salaId, 40);
        turmaRepositorio.salvar(outra);
    }

    @Quando("a coordenação tenta vincular a sala com conflito")
    public void tentar_vincular_sala_com_conflito() {
        try {
            ofertaTurmaServico.vincularSala(turma.getId(), salaId);
        } catch (RuntimeException e) {
            excecaoLocal = e;
        }
    }

    @Entao("o sistema deve rejeitar informando conflito de horário da sala")
    public void rejeitar_conflito_sala() {
        assertNotNull(excecaoLocal);
        assertTrue(excecaoLocal.getMessage().contains("conflito de horário da sala"),
                "Mensagem obtida: " + excecaoLocal.getMessage());
    }
}
