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

public class EncerrarPeriodoLetivoFuncionalidade extends PeriodoLetivoFuncionalidade {

    private final CursoId cursoId = new CursoId(1);
    private PeriodoLetivoId periodoId;
    private RuntimeException excecao;

    @Dado("um período letivo cadastrado pronto para encerramento")
    public void periodo_pronto_para_encerramento() {
        var periodo = periodoLetivoServico.cadastrar(
                cursoId, 2025, 1,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 30));
        periodoId = periodo.getId();
    }

    @Dado("um período letivo já encerrado anteriormente")
    public void periodo_ja_encerrado() {
        var periodo = periodoLetivoServico.cadastrar(
                cursoId, 2025, 1,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 30));
        periodoId = periodo.getId();
        verificadorPendencias.setPendencias(false);
        periodoLetivoServico.encerrar(periodoId);
    }

    @Dado("sem pendências que impedem o encerramento")
    public void sem_pendencias() {
        verificadorPendencias.setPendencias(false);
    }

    @Dado("com pendências que impedem o encerramento")
    public void com_pendencias() {
        verificadorPendencias.setPendencias(true);
    }

    @Quando("a secretaria encerra o período letivo")
    public void encerrar_periodo() {
        periodoLetivoServico.encerrar(periodoId);
    }

    @Quando("a secretaria tenta encerrar o período letivo")
    public void tentar_encerrar_periodo() {
        try {
            periodoLetivoServico.encerrar(periodoId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o período letivo deve ter status encerrado")
    public void periodo_deve_ter_status_encerrado() {
        var periodo = repositorio.obter(periodoId);
        assertEquals(StatusPeriodoLetivo.ENCERRADO, periodo.getStatus());
    }

    @Entao("o sistema rejeita o encerramento informando pendências")
    public void sistema_rejeita_encerramento_por_pendencias() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
    }

    @Entao("o sistema rejeita o encerramento informando status inválido")
    public void sistema_rejeita_encerramento_por_status() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
    }
}
