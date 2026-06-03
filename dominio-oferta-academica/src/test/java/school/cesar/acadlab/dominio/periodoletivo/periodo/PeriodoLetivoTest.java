package school.cesar.acadlab.dominio.periodoletivo.periodo;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.periodoletivo.StatusPeriodoLetivo;
import school.cesar.acadlab.dominio.periodoletivo.TipoJanela;
import school.cesar.acadlab.dominio.periodoletivo.curso.CursoId;
import school.cesar.acadlab.dominio.periodoletivo.periodo.PeriodoLetivo.PeriodoLetivoCanceladoEvento;
import school.cesar.acadlab.dominio.periodoletivo.periodo.PeriodoLetivo.PeriodoLetivoEncerradoEvento;

class PeriodoLetivoTest {

    private final PeriodoLetivoId id = new PeriodoLetivoId(1);
    private final CursoId cursoId = new CursoId(1);
    private final LocalDate hoje = LocalDate.now();

    private PeriodoLetivo criarPeriodo() {
        return new PeriodoLetivo(id, cursoId, 2026, 1, hoje, hoje.plusMonths(6));
    }

    @Test
    void novoPeriodo_deveIniciarComStatusNaoIniciado() {
        var periodo = criarPeriodo();
        assertEquals(StatusPeriodoLetivo.NAO_INICIADO, periodo.getStatus());
    }

    @Test
    void definirJanela_deveAdicionarJanelaAoPeriodo() {
        var periodo = criarPeriodo();

        var evento = periodo.definirJanela(TipoJanela.MATRICULA, hoje, hoje.plusDays(10));

        assertNotNull(evento);
        assertTrue(periodo.buscarJanela(TipoJanela.MATRICULA).isPresent());
    }

    @Test
    void definirJanela_comMesmoTipo_deveSubstituirJanelaAnterior() {
        var periodo = criarPeriodo();
        periodo.definirJanela(TipoJanela.MATRICULA, hoje, hoje.plusDays(5));

        periodo.definirJanela(TipoJanela.MATRICULA, hoje.plusDays(1), hoje.plusDays(10));

        assertEquals(1, periodo.getJanelas().stream()
                .filter(j -> j.getTipo() == TipoJanela.MATRICULA).count());
    }

    @Test
    void janelaAtiva_comDataDentroDoIntervalo_deveRetornarTrue() {
        var periodo = criarPeriodo();
        periodo.definirJanela(TipoJanela.MATRICULA, hoje, hoje.plusDays(10));

        assertTrue(periodo.janelaAtiva(TipoJanela.MATRICULA, hoje.plusDays(5)));
    }

    @Test
    void cancelar_comStatusNaoIniciado_deveAlterarStatusParaCancelado() {
        var periodo = criarPeriodo();

        var evento = periodo.cancelar();

        assertNotNull(evento);
        assertInstanceOf(PeriodoLetivoCanceladoEvento.class, evento);
        assertEquals(StatusPeriodoLetivo.CANCELADO, periodo.getStatus());
    }

    @Test
    void cancelar_comStatusDiferenteDeNaoIniciado_deveLancarExcecao() {
        var periodo = criarPeriodo();
        periodo.encerrar();

        assertThrows(IllegalStateException.class, periodo::cancelar);
    }

    @Test
    void encerrar_deveAlterarStatusParaEncerrado() {
        var periodo = criarPeriodo();

        var evento = periodo.encerrar();

        assertNotNull(evento);
        assertInstanceOf(PeriodoLetivoEncerradoEvento.class, evento);
        assertEquals(StatusPeriodoLetivo.ENCERRADO, periodo.getStatus());
    }

    @Test
    void editar_comStatusNaoIniciado_deveAtualizarDatas() {
        var periodo = criarPeriodo();
        var novaInicio = hoje.plusDays(5);
        var novaFim = hoje.plusMonths(7);

        var evento = periodo.editar(novaInicio, novaFim);

        assertNotNull(evento);
        assertEquals(novaInicio, periodo.getDataInicio());
        assertEquals(novaFim, periodo.getDataFim());
    }

    @Test
    void editar_comStatusEncerrado_deveLancarExcecao() {
        var periodo = criarPeriodo();
        periodo.encerrar();

        assertThrows(IllegalStateException.class,
                () -> periodo.editar(hoje, hoje.plusMonths(5)));
    }
}
