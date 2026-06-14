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
    private RuntimeException excecao;

    // -----------------------------------------------------------------------
    // Shared Given step
    // -----------------------------------------------------------------------

    @Dado("uma turma planejada com horario segunda 08h as 10h no periodo 1")
    public void turma_planejada_com_horario() {
        turma = ofertaTurmaServico.ofertar(periodoId, disciplinaId, ModalidadeTurma.PRESENCIAL, 30);
        ofertaTurmaServico.adicionarHorario(turma.getId(),
                DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));
        turma = turmaRepositorio.obter(turma.getId());
    }

    // -----------------------------------------------------------------------
    // Scenario 1 — vincular professor sem conflito
    // -----------------------------------------------------------------------

    @Dado("um professor sem turmas no mesmo periodo")
    public void professor_sem_turmas() {
        professorId = professorRepositorio.proximoId();
        professorRepositorio.salvar(new Professor(professorId, "Prof. Sem Conflito"));
    }

    @Quando("a coordenacao adiciona o horario e vincula o professor a turma")
    public void vincular_professor_sem_conflito() {
        try {
            ofertaTurmaServico.vincularProfessor(turma.getId(), professorId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o professor e vinculado com sucesso")
    public void professor_vinculado_com_sucesso() {
        assertNull(excecao, "Nao deveria ter lancado excecao");
        var turmaAtualizada = turmaRepositorio.obter(turma.getId());
        assertEquals(professorId, turmaAtualizada.getProfessorId());
    }

    // -----------------------------------------------------------------------
    // Scenario 2 — conflito de horario do professor
    // -----------------------------------------------------------------------

    @Dado("o professor ja tem turma com horario segunda 09h as 11h no mesmo periodo")
    public void professor_com_turma_conflitante() {
        professorId = professorRepositorio.proximoId();
        professorRepositorio.salvar(new Professor(professorId, "Prof. Ocupado"));

        // Cria outra turma com horario conflitante e vincula o professor diretamente
        var outraId = turmaRepositorio.proximoId();
        var outra = new Turma(outraId, periodoId, new DisciplinaId(2), ModalidadeTurma.PRESENCIAL, 30);
        outra.adicionarHorario(new HorarioAula(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0)));
        outra.vincularProfessor(professorId);
        turmaRepositorio.salvar(outra);
    }

    @Quando("a coordenacao tenta vincular o professor com conflito")
    public void tentar_vincular_professor_com_conflito() {
        try {
            ofertaTurmaServico.vincularProfessor(turma.getId(), professorId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema rejeita a vinculacao informando conflito de horario do professor")
    public void rejeitar_conflito_professor() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN6"),
                "Mensagem esperada conter RN6, mas foi: " + excecao.getMessage());
    }

    // -----------------------------------------------------------------------
    // Scenario 3 — conflito de horario da sala
    // -----------------------------------------------------------------------

    @Dado("a sala ja esta alocada em turma com horario segunda 09h as 11h no mesmo periodo")
    public void sala_com_turma_conflitante() {
        salaId = salaRepositorio.proximoId();
        salaRepositorio.salvar(new Sala(salaId, "Sala Ocupada", 40));

        // Cria outra turma com horario conflitante e vincula a sala diretamente
        var outraId = turmaRepositorio.proximoId();
        var outra = new Turma(outraId, periodoId, new DisciplinaId(2), ModalidadeTurma.PRESENCIAL, 30);
        outra.adicionarHorario(new HorarioAula(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0)));
        outra.vincularSala(salaId, 40);
        turmaRepositorio.salvar(outra);
    }

    @Quando("a coordenacao tenta vincular a sala com conflito")
    public void tentar_vincular_sala_com_conflito() {
        try {
            ofertaTurmaServico.vincularSala(turma.getId(), salaId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema rejeita a vinculacao informando conflito de horario da sala")
    public void rejeitar_conflito_sala() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN7"),
                "Mensagem esperada conter RN7, mas foi: " + excecao.getMessage());
    }
}
