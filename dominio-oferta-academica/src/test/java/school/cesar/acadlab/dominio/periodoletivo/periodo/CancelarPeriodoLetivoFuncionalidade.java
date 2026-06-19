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

public class CancelarPeriodoLetivoFuncionalidade {

    private final PeriodoLetivoFuncionalidade ctx;
    private final CursoId cursoId = new CursoId(1);
    private PeriodoLetivoId periodoId;

    public CancelarPeriodoLetivoFuncionalidade(PeriodoLetivoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um período letivo cadastrado passível de cancelamento")
    public void periodo_passivel_de_cancelamento() {
        var periodo = ctx.periodoLetivoServico.cadastrar(
                cursoId, 2025, 1,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 30));
        periodoId = periodo.getId();
    }

    @Dado("um período letivo já encerrado para tentativa de cancelamento")
    public void periodo_ja_encerrado_para_cancelamento() {
        var periodo = ctx.periodoLetivoServico.cadastrar(
                cursoId, 2025, 1,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 30));
        periodoId = periodo.getId();
        ctx.verificadorPendencias.setPendencias(false);
        ctx.periodoLetivoServico.encerrar(periodoId);
    }

    @Dado("um período letivo em andamento para tentativa de cancelamento")
    public void periodo_em_andamento_para_cancelamento() {
        var periodo = ctx.periodoLetivoServico.cadastrar(
                cursoId, 2025, 1,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 30));
        periodoId = periodo.getId();
        ctx.periodoLetivoServico.iniciar(periodoId);
    }

    @Dado("sem matrículas confirmadas no período")
    public void sem_matriculas_confirmadas() {
        ctx.verificadorMatriculas.setMatriculas(false);
    }

    @Dado("com matrículas confirmadas que impedem o cancelamento")
    public void com_matriculas_confirmadas() {
        ctx.verificadorMatriculas.setMatriculas(true);
    }

    @Quando("a secretaria cancela o período letivo")
    public void cancelar_periodo() {
        ctx.periodoLetivoServico.cancelar(periodoId);
    }

    @Quando("a secretaria tenta cancelar o período letivo")
    public void tentar_cancelar_periodo() {
        try {
            ctx.periodoLetivoServico.cancelar(periodoId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o período letivo deve ter status cancelado")
    public void periodo_deve_ter_status_cancelado() {
        var periodo = ctx.repositorio.obter(periodoId);
        assertEquals(StatusPeriodoLetivo.CANCELADO, periodo.getStatus());
    }
}
