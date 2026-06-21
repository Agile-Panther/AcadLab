package school.cesar.acadlab.dominio.periodoletivo.periodo;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoFuncionalidade;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.periodoletivo.curso.CursoId;

public class EditarPeriodoLetivoFuncionalidade {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final PeriodoLetivoFuncionalidade ctx;
    private final CursoId cursoId = new CursoId(1);
    private PeriodoLetivoId periodoId;

    public EditarPeriodoLetivoFuncionalidade(PeriodoLetivoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um período letivo cadastrado editável")
    public void periodo_editavel() {
        var inicio = LocalDate.now().plusYears(1);
        var fim = inicio.plusMonths(6);
        var periodo = ctx.periodoLetivoServico.cadastrar(cursoId, 2028, 1, inicio, fim);
        periodoId = periodo.getId();
    }

    @Dado("um período letivo com status encerrado")
    public void periodo_com_status_encerrado() {
        var periodo = ctx.periodoLetivoServico.cadastrar(
                cursoId, 2025, 1,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 30));
        periodoId = periodo.getId();
        ctx.verificadorPendencias.setPendencias(false);
        ctx.periodoLetivoServico.encerrar(periodoId);
    }

    @Dado("um período letivo em andamento para tentativa de edição")
    public void periodo_em_andamento_para_edicao() {
        var periodo = ctx.periodoLetivoServico.cadastrar(
                cursoId, 2025, 1,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 30));
        periodoId = periodo.getId();
        ctx.periodoLetivoServico.iniciar(periodoId);
    }

    @E("um segundo período letivo com datas sobrepostas cadastrado no mesmo curso")
    public void segundo_periodo_com_datas_sobrepostas() {
        ctx.periodoLetivoServico.cadastrar(cursoId, 2029, 1,
                LocalDate.of(2029, 1, 1), LocalDate.of(2029, 12, 31));
    }

    @Quando("a secretaria edita o período letivo para as datas de {string} a {string}")
    public void editar_periodo(String inicio, String fim) {
        ctx.periodoLetivoServico.editar(periodoId,
                LocalDate.parse(inicio, FMT), LocalDate.parse(fim, FMT));
    }

    @Quando("a secretaria tenta editar o período letivo para as datas de {string} a {string}")
    public void tentar_editar_periodo(String inicio, String fim) {
        try {
            ctx.periodoLetivoServico.editar(periodoId,
                    LocalDate.parse(inicio, FMT), LocalDate.parse(fim, FMT));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o período letivo deve ter data de início {string} e data de fim {string}")
    public void periodo_deve_ter_datas(String inicio, String fim) {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        var periodo = ctx.repositorio.obter(periodoId);
        assertEquals(LocalDate.parse(inicio, FMT), periodo.getDataInicio());
        assertEquals(LocalDate.parse(fim, FMT), periodo.getDataFim());
    }
}
