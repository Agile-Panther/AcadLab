package school.cesar.acadlab.dominio.ofertaturmas.turma;

import static org.junit.jupiter.api.Assertions.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.ofertaturmas.DisciplinaId;
import school.cesar.acadlab.dominio.ofertaturmas.PeriodoLetivoId;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorId;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.Turma.TurmaOfertadaEvento;

class TurmaTest {

    private final TurmaId id = new TurmaId(1);
    private final PeriodoLetivoId periodoId = new PeriodoLetivoId(1);
    private final DisciplinaId disciplinaId = new DisciplinaId(1);
    private final ProfessorId professorId = new ProfessorId(1);
    private final SalaId salaId = new SalaId(1);

    private Turma criarTurma() {
        return new Turma(id, periodoId, disciplinaId, ModalidadeTurma.PRESENCIAL, 30);
    }

    @Test
    void novaTurma_deveIniciarComStatusPlanejada() {
        var turma = criarTurma();
        assertEquals(StatusTurma.PLANEJADA, turma.getStatus());
    }

    @Test
    void vincularSala_comCapacidadeSalaInsuficiente_deveLancarExcecao() {
        var turma = criarTurma();

        var excecao = assertThrows(IllegalStateException.class,
                () -> turma.vincularSala(salaId, 20));
        assertNotNull(excecao.getMessage());
    }

    @Test
    void vincularSala_comCapacidadeSalaAdequada_deveVincularComSucesso() {
        var turma = criarTurma();

        var evento = turma.vincularSala(salaId, 40);

        assertNotNull(evento);
        assertEquals(salaId, turma.getSalaId());
    }

    @Test
    void ofertar_semProfessor_deveLancarExcecao() {
        var turma = criarTurma();
        turma.vincularSala(salaId, 40);
        turma.adicionarHorario(new HorarioAula(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0)));

        assertThrows(IllegalStateException.class, turma::ofertar);
    }

    @Test
    void ofertar_comTodasConfiguracoesPresentes_deveAlterarStatusParaOfertada() {
        var turma = criarTurma();
        turma.vincularProfessor(professorId);
        turma.vincularSala(salaId, 40);
        turma.adicionarHorario(new HorarioAula(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0)));

        var evento = turma.ofertar();

        assertNotNull(evento);
        assertInstanceOf(TurmaOfertadaEvento.class, evento);
        assertEquals(StatusTurma.OFERTADA, turma.getStatus());
    }

    @Test
    void cancelar_deveMudarStatusParaCancelada() {
        var turma = criarTurma();

        var evento = turma.cancelar();

        assertNotNull(evento);
        assertEquals(StatusTurma.CANCELADA, turma.getStatus());
    }

    @Test
    void inativar_deveMudarStatusParaInativa() {
        var turma = criarTurma();

        var evento = turma.inativar();

        assertNotNull(evento);
        assertEquals(StatusTurma.INATIVA, turma.getStatus());
    }

    @Test
    void ofertar_turmaInativa_deveLancarExcecao() {
        var turma = criarTurma();
        turma.inativar();

        assertThrows(IllegalStateException.class, turma::ofertar);
    }

    @Test
    void alterarModalidade_comModalidadeValida_deveAtualizarTurma() {
        var turma = criarTurma();

        turma.alterarModalidade(ModalidadeTurma.EAD);

        assertEquals(ModalidadeTurma.EAD, turma.getModalidade());
    }

    @Test
    void configurarListaEspera_deveHabilitarEDesabilitarSemPendencias() {
        var turma = criarTurma();

        turma.habilitarListaEspera();
        assertTrue(turma.isListaEsperaHabilitada());

        turma.desabilitarListaEspera(0);
        assertFalse(turma.isListaEsperaHabilitada());
    }

    @Test
    void desabilitarListaEspera_comEstudantesPendentes_deveLancarExcecao() {
        var turma = criarTurma();
        turma.habilitarListaEspera();

        assertThrows(IllegalStateException.class, () -> turma.desabilitarListaEspera(1));
        assertTrue(turma.isListaEsperaHabilitada());
    }

    @Test
    void horarioAula_conflitaCom_comMesmoDiaEHorarioSobreposto_deveRetornarTrue() {
        var h1 = new HorarioAula(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));
        var h2 = new HorarioAula(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0));

        assertTrue(h1.conflitaCom(h2));
    }

    @Test
    void horarioAula_conflitaCom_comDiasDiferentes_deveRetornarFalse() {
        var h1 = new HorarioAula(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));
        var h2 = new HorarioAula(DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));

        assertFalse(h1.conflitaCom(h2));
    }
}
