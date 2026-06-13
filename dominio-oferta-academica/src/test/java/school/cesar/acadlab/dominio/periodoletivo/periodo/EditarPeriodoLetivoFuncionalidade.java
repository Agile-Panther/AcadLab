package school.cesar.acadlab.dominio.periodoletivo.periodo;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoFuncionalidade;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.periodoletivo.curso.CursoId;

public class EditarPeriodoLetivoFuncionalidade extends PeriodoLetivoFuncionalidade {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final CursoId cursoId = new CursoId(1);
    private PeriodoLetivoId periodoId;
    private RuntimeException excecao;

    @Dado("um período letivo cadastrado editável")
    public void periodo_editavel() {
        var inicio = LocalDate.now().plusYears(1);
        var fim = inicio.plusMonths(6);
        var periodo = periodoLetivoServico.cadastrar(cursoId, 2028, 1, inicio, fim);
        periodoId = periodo.getId();
    }

    @Dado("um período letivo com status encerrado")
    public void periodo_com_status_encerrado() {
        var periodo = periodoLetivoServico.cadastrar(
                cursoId, 2025, 1,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 30));
        periodoId = periodo.getId();
        verificadorPendencias.setPendencias(false);
        periodoLetivoServico.encerrar(periodoId);
    }

    @Dado("um período letivo em andamento para tentativa de edição")
    public void periodo_em_andamento_para_edicao() {
        var periodo = periodoLetivoServico.cadastrar(
                cursoId, 2025, 1,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 30));
        periodoId = periodo.getId();
        periodoLetivoServico.iniciar(periodoId);
    }

    @Quando("a secretaria edita o período letivo para as datas de {string} a {string}")
    public void editar_periodo(String inicio, String fim) {
        periodoLetivoServico.editar(periodoId,
                LocalDate.parse(inicio, FMT), LocalDate.parse(fim, FMT));
    }

    @Quando("a secretaria tenta editar o período letivo para as datas de {string} a {string}")
    public void tentar_editar_periodo(String inicio, String fim) {
        try {
            periodoLetivoServico.editar(periodoId,
                    LocalDate.parse(inicio, FMT), LocalDate.parse(fim, FMT));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o período letivo deve ter data de início {string} e data de fim {string}")
    public void periodo_deve_ter_datas(String inicio, String fim) {
        assertNull(excecao, "Não deveria ter lançado exceção");
        var periodo = repositorio.obter(periodoId);
        assertEquals(LocalDate.parse(inicio, FMT), periodo.getDataInicio());
        assertEquals(LocalDate.parse(fim, FMT), periodo.getDataFim());
    }

    @Entao("o sistema rejeita a edição informando status inválido")
    public void sistema_rejeita_edicao_por_status() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
    }
}
