package school.cesar.acadlab.dominio.periodoletivo.periodo;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoFuncionalidade;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.periodoletivo.StatusPeriodoLetivo;
import school.cesar.acadlab.dominio.periodoletivo.curso.CursoId;

public class CancelarPeriodoLetivoFuncionalidade extends PeriodoLetivoFuncionalidade {

    private final CursoId cursoId = new CursoId(1);
    private PeriodoLetivoId periodoId;
    private RuntimeException excecao;

    @Dado("um período letivo cadastrado passível de cancelamento")
    public void periodo_passivel_de_cancelamento() {
        var periodo = periodoLetivoServico.cadastrar(
                cursoId, 2025, 1,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 30));
        periodoId = periodo.getId();
    }

    @Dado("um período letivo já encerrado para tentativa de cancelamento")
    public void periodo_ja_encerrado_para_cancelamento() {
        var periodo = periodoLetivoServico.cadastrar(
                cursoId, 2025, 1,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 30));
        periodoId = periodo.getId();
        verificadorPendencias.setPendencias(false);
        periodoLetivoServico.encerrar(periodoId);
    }

    @Dado("um período letivo em andamento para tentativa de cancelamento")
    public void periodo_em_andamento_para_cancelamento() {
        var periodo = periodoLetivoServico.cadastrar(
                cursoId, 2025, 1,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 30));
        periodoId = periodo.getId();
        periodoLetivoServico.iniciar(periodoId);
    }

    @Dado("sem matrículas confirmadas no período")
    public void sem_matriculas_confirmadas() {
        verificadorMatriculas.setMatriculas(false);
    }

    @Dado("com matrículas confirmadas que impedem o cancelamento")
    public void com_matriculas_confirmadas() {
        verificadorMatriculas.setMatriculas(true);
    }

    @Quando("a secretaria cancela o período letivo")
    public void cancelar_periodo() {
        periodoLetivoServico.cancelar(periodoId);
    }

    @Quando("a secretaria tenta cancelar o período letivo")
    public void tentar_cancelar_periodo() {
        try {
            periodoLetivoServico.cancelar(periodoId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o período letivo deve ter status cancelado")
    public void periodo_deve_ter_status_cancelado() {
        var periodo = repositorio.obter(periodoId);
        assertEquals(StatusPeriodoLetivo.CANCELADO, periodo.getStatus());
    }

    @Entao("o sistema rejeita o cancelamento informando matrículas confirmadas")
    public void sistema_rejeita_cancelamento_por_matriculas() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
    }

    @Entao("o sistema rejeita o cancelamento informando status inválido")
    public void sistema_rejeita_cancelamento_por_status() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
    }
}
