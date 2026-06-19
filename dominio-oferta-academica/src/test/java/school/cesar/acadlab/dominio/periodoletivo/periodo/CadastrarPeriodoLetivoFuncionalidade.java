package school.cesar.acadlab.dominio.periodoletivo.periodo;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoFuncionalidade;
import school.cesar.acadlab.dominio.periodoletivo.StatusPeriodoLetivo;
import school.cesar.acadlab.dominio.periodoletivo.curso.CursoId;

public class CadastrarPeriodoLetivoFuncionalidade {

    private final PeriodoLetivoFuncionalidade ctx;
    private final CursoId cursoId = new CursoId(1);
    private PeriodoLetivo periodoCriado;

    public CadastrarPeriodoLetivoFuncionalidade(PeriodoLetivoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("que não existe período letivo cadastrado para o curso")
    public void sem_periodo_cadastrado() {
        // repositório começa vazio
    }

    @Dado("que já existe um período letivo com datas sobrepostas para o mesmo curso")
    public void com_periodo_sobreposto() {
        var hoje = LocalDate.now();
        ctx.periodoLetivoServico.cadastrar(cursoId, 2026, 1, hoje, hoje.plusMonths(6));
    }

    @Quando("a secretaria cadastra um novo período letivo com datas válidas")
    public void cadastrar_periodo_valido() {
        try {
            var futuro = LocalDate.now().plusYears(2);
            periodoCriado = ctx.periodoLetivoServico.cadastrar(cursoId, 2028, 1, futuro, futuro.plusMonths(6));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("a secretaria tenta cadastrar um período letivo com datas sobrepostas")
    public void cadastrar_periodo_sobreposto() {
        try {
            var hoje = LocalDate.now();
            periodoCriado = ctx.periodoLetivoServico.cadastrar(cursoId, 2026, 1, hoje, hoje.plusMonths(6));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o período letivo é cadastrado com status não iniciado")
    public void periodo_cadastrado_com_sucesso() {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        assertNotNull(periodoCriado);
        assertEquals(StatusPeriodoLetivo.NAO_INICIADO, periodoCriado.getStatus());
    }
}
