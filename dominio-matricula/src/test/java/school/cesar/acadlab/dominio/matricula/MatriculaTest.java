package school.cesar.acadlab.dominio.matricula;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import school.cesar.acadlab.dominio.matricula.matricula.CoordenadorId;
import school.cesar.acadlab.dominio.matricula.matricula.DisciplinaId;
import school.cesar.acadlab.dominio.matricula.matricula.EstudanteId;
import school.cesar.acadlab.dominio.matricula.matricula.HorarioAula;
import school.cesar.acadlab.dominio.matricula.matricula.Matricula;
import school.cesar.acadlab.dominio.matricula.matricula.MatriculaId;
import school.cesar.acadlab.dominio.matricula.matricula.PeriodoLetivoId;
import school.cesar.acadlab.dominio.matricula.matricula.StatusItemMatricula;
import school.cesar.acadlab.dominio.matricula.matricula.StatusMatricula;
import school.cesar.acadlab.dominio.matricula.matricula.TurmaId;

class MatriculaTest {

    private Matricula matricula;

    private static final LocalDate INICIO_JANELA = LocalDate.of(2025, 1, 10);
    private static final LocalDate FIM_JANELA = LocalDate.of(2025, 1, 20);
    private static final LocalDate DENTRO_JANELA = LocalDate.of(2025, 1, 15);
    private static final LocalDate INICIO_AJUSTE = LocalDate.of(2025, 2, 1);
    private static final LocalDate FIM_AJUSTE = LocalDate.of(2025, 2, 5);
    private static final LocalDate INICIO_TRANCAMENTO = LocalDate.of(2025, 3, 1);
    private static final LocalDate FIM_TRANCAMENTO = LocalDate.of(2025, 3, 15);

    @BeforeEach
    void setUp() {
        matricula = new Matricula(new MatriculaId(1), new EstudanteId(1),
                new PeriodoLetivoId(1), 24);
    }

    @Test
    void deveIniciarEmMontagem() {
        assertEquals(StatusMatricula.EM_MONTAGEM, matricula.getStatus());
    }

    @Test
    void rn1_deveAdicionarItemDentroJanela() {
        matricula.adicionarItem(new TurmaId(1), new DisciplinaId(1), 4,
                List.of(), true, true, false, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA);
        assertEquals(1, matricula.getItens().size());
    }

    @Test
    void rn1_deveLancarErroForaDaJanela() {
        assertThrows(IllegalArgumentException.class, () ->
                matricula.adicionarItem(new TurmaId(1), new DisciplinaId(1), 4,
                        List.of(), true, true, false,
                        LocalDate.of(2025, 2, 1), INICIO_JANELA, FIM_JANELA));
    }

    @Test
    void rn2_deveLancarErroSemPreRequisitos() {
        assertThrows(IllegalArgumentException.class, () ->
                matricula.adicionarItem(new TurmaId(1), new DisciplinaId(1), 4,
                        List.of(), false, true, false, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA));
    }

    @Test
    void rn3_deveLancarErroSemCorrequisitos() {
        assertThrows(IllegalArgumentException.class, () ->
                matricula.adicionarItem(new TurmaId(1), new DisciplinaId(1), 4,
                        List.of(), true, false, false, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA));
    }

    @Test
    void rn4_deveLancarErroExcedendoLimiteCreditos() {
        assertThrows(IllegalArgumentException.class, () ->
                matricula.adicionarItem(new TurmaId(1), new DisciplinaId(1), 25,
                        List.of(), true, true, false, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA));
    }

    @Test
    void rn5_deveLancarErroComPendencias() {
        assertThrows(IllegalArgumentException.class, () ->
                matricula.adicionarItem(new TurmaId(1), new DisciplinaId(1), 4,
                        List.of(), true, true, true, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA));
    }

    @Test
    void rn6_deveConfirmarComVagasDisponiveis() {
        adicionarTurmaComHorario(1, DayOfWeek.MONDAY, "08:00", "10:00");
        matricula.confirmar(Map.of(new TurmaId(1), 30));
        assertEquals(StatusMatricula.CONFIRMADA, matricula.getStatus());
    }

    @Test
    void rn6_deveLancarErroSemVagas() {
        adicionarTurmaComHorario(1, DayOfWeek.MONDAY, "08:00", "10:00");
        assertThrows(IllegalArgumentException.class, () ->
                matricula.confirmar(Map.of(new TurmaId(1), 0)));
    }

    @Test
    void rn7_deveLancarErroComConflitoDeHorario() {
        adicionarTurmaComHorario(1, DayOfWeek.MONDAY, "08:00", "10:00");
        adicionarTurmaComHorario(2, DayOfWeek.MONDAY, "09:00", "11:00");
        assertThrows(IllegalArgumentException.class, () ->
                matricula.confirmar(Map.of(new TurmaId(1), 30, new TurmaId(2), 30)));
    }

    @Test
    void rn8_deveCancelarItemDentroJanelaAjuste() {
        adicionarTurmaSimples(1);
        matricula.confirmar(Map.of(new TurmaId(1), 30));
        matricula.cancelarItem(new TurmaId(1), LocalDate.of(2025, 2, 3), INICIO_AJUSTE, FIM_AJUSTE);
        assertEquals(StatusItemMatricula.CANCELADO, matricula.getItens().get(0).getStatus());
    }

    @Test
    void rn8_deveLancarErroAjusteForaJanela() {
        adicionarTurmaSimples(1);
        matricula.confirmar(Map.of(new TurmaId(1), 30));
        assertThrows(IllegalArgumentException.class, () ->
                matricula.cancelarItem(new TurmaId(1), LocalDate.of(2025, 3, 1),
                        INICIO_AJUSTE, FIM_AJUSTE));
    }

    @Test
    void rn9_deveTrancarDisciplinaDentroJanela() {
        adicionarTurmaSimples(1);
        matricula.confirmar(Map.of(new TurmaId(1), 30));
        matricula.trancarDisciplina(new TurmaId(1), LocalDate.of(2025, 3, 5),
                INICIO_TRANCAMENTO, FIM_TRANCAMENTO);
        assertEquals(StatusItemMatricula.TRANCADO, matricula.getItens().get(0).getStatus());
    }

    @Test
    void rn9_deveLancarErroTrancamentoForaJanela() {
        adicionarTurmaSimples(1);
        matricula.confirmar(Map.of(new TurmaId(1), 30));
        assertThrows(IllegalArgumentException.class, () ->
                matricula.trancarDisciplina(new TurmaId(1), LocalDate.of(2025, 4, 1),
                        INICIO_TRANCAMENTO, FIM_TRANCAMENTO));
    }

    @Test
    void rn10_devePermitirAdicionarComExcecaoDeferida() {
        matricula.solicitarExcecao(new DisciplinaId(5), "Necessidade especial");
        matricula.deferir(new DisciplinaId(5), new CoordenadorId(1));
        matricula.adicionarItem(new TurmaId(10), new DisciplinaId(5), 4,
                List.of(), false, false, true, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA);
        assertEquals(1, matricula.getItens().size());
    }

    @Test
    void rn10_deveLancarErroSemExcecaoDeferida() {
        matricula.solicitarExcecao(new DisciplinaId(5), "Necessidade especial");
        assertThrows(IllegalArgumentException.class, () ->
                matricula.adicionarItem(new TurmaId(10), new DisciplinaId(5), 4,
                        List.of(), false, false, true, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA));
    }

    private void adicionarTurmaSimples(int id) {
        matricula.adicionarItem(new TurmaId(id), new DisciplinaId(id), 4,
                List.of(), true, true, false, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA);
    }

    private void adicionarTurmaComHorario(int id, DayOfWeek dia, String inicio, String fim) {
        HorarioAula horario = new HorarioAula(dia, LocalTime.parse(inicio), LocalTime.parse(fim));
        matricula.adicionarItem(new TurmaId(id), new DisciplinaId(id), 4,
                List.of(horario), true, true, false, DENTRO_JANELA, INICIO_JANELA, FIM_JANELA);
    }
}
