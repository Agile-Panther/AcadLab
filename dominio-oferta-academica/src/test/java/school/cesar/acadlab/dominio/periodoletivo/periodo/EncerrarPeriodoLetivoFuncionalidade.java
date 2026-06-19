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

public class EncerrarPeriodoLetivoFuncionalidade {

    private final PeriodoLetivoFuncionalidade ctx;
    private final CursoId cursoId = new CursoId(1);
    private PeriodoLetivoId periodoId;

    public EncerrarPeriodoLetivoFuncionalidade(PeriodoLetivoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um período letivo cadastrado pronto para encerramento")
    public void periodo_pronto_para_encerramento() {
        var periodo = ctx.periodoLetivoServico.cadastrar(
                cursoId, 2025, 1,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 30));
        periodoId = periodo.getId();
    }

    @Dado("um período letivo já encerrado anteriormente")
    public void periodo_ja_encerrado() {
        var periodo = ctx.periodoLetivoServico.cadastrar(
                cursoId, 2025, 1,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 30));
        periodoId = periodo.getId();
        ctx.verificadorPendencias.setPendencias(false);
        ctx.periodoLetivoServico.encerrar(periodoId);
    }

    @Dado("sem pendências que impedem o encerramento")
    public void sem_pendencias() {
        ctx.verificadorPendencias.setPendencias(false);
    }

    @Dado("com pendências que impedem o encerramento")
    public void com_pendencias() {
        ctx.verificadorPendencias.setPendencias(true);
    }

    @Quando("a secretaria encerra o período letivo")
    public void encerrar_periodo() {
        ctx.periodoLetivoServico.encerrar(periodoId);
    }

    @Quando("a secretaria tenta encerrar o período letivo")
    public void tentar_encerrar_periodo() {
        try {
            ctx.periodoLetivoServico.encerrar(periodoId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o período letivo deve ter status encerrado")
    public void periodo_deve_ter_status_encerrado() {
        var periodo = ctx.repositorio.obter(periodoId);
        assertEquals(StatusPeriodoLetivo.ENCERRADO, periodo.getStatus());
    }
}
