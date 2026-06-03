package school.cesar.acadlab.dominio.periodoletivo.janelaacademica;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoFuncionalidade;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.periodoletivo.TipoJanela;
import school.cesar.acadlab.dominio.periodoletivo.curso.CursoId;

public class DefinirJanelasAcademicasFuncionalidade extends PeriodoLetivoFuncionalidade {

    private final CursoId cursoId = new CursoId(1);
    private PeriodoLetivoId periodoId;
    private RuntimeException excecao;

    @Dado("um período letivo cadastrado sem janelas definidas")
    public void periodo_sem_janelas() {
        var hoje = LocalDate.now();
        var periodo = periodoLetivoServico.cadastrar(cursoId, 2026, 1, hoje, hoje.plusMonths(6));
        periodoId = periodo.getId();
    }

    @Quando("a secretaria define a janela de matrícula com datas válidas")
    public void definir_janela_matricula() {
        try {
            var hoje = LocalDate.now();
            periodoLetivoServico.definirJanela(periodoId, TipoJanela.MATRICULA, hoje, hoje.plusDays(10));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Quando("a secretaria redefine a janela de matrícula com novas datas")
    public void redefinir_janela_matricula() {
        try {
            var hoje = LocalDate.now();
            periodoLetivoServico.definirJanela(periodoId, TipoJanela.MATRICULA, hoje, hoje.plusDays(10));
            periodoLetivoServico.definirJanela(periodoId, TipoJanela.MATRICULA, hoje.plusDays(1), hoje.plusDays(15));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("a janela de matrícula é registrada no período letivo")
    public void janela_registrada() {
        assertNull(excecao, "Não deveria ter lançado exceção");
        var periodo = consultaServico.buscar(periodoId);
        assertTrue(periodo.buscarJanela(TipoJanela.MATRICULA).isPresent());
    }

    @Entao("apenas uma janela de matrícula existe no período letivo")
    public void apenas_uma_janela_matricula() {
        assertNull(excecao, "Não deveria ter lançado exceção");
        var periodo = consultaServico.buscar(periodoId);
        var janelaMatricula = periodo.getJanelas().stream()
                .filter(j -> j.getTipo() == TipoJanela.MATRICULA)
                .count();
        assertEquals(1, janelaMatricula);
    }
}
