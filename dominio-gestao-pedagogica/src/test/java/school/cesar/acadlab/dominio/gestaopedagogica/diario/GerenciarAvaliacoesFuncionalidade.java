package school.cesar.acadlab.dominio.gestaopedagogica.diario;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.gestaopedagogica.GestaoPedagogicaFuncionalidade;

public class GerenciarAvaliacoesFuncionalidade {

    private final GestaoPedagogicaFuncionalidade ctx;

    private static final LocalDate INICIO = LocalDate.of(2025, 2, 1);
    private static final LocalDate FIM = LocalDate.of(2025, 7, 31);

    private final TurmaId turmaId = new TurmaId(20);
    private final PeriodoLetivoId periodoId = new PeriodoLetivoId(20);
    private final ProfessorId professorResponsavel = new ProfessorId(20);

    private DiarioTurma diario;

    public GerenciarAvaliacoesFuncionalidade(GestaoPedagogicaFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um diário de turma vazio para gerenciamento de avaliações")
    public void diario_vazio_para_avaliacoes() {
        diario = new DiarioTurma(ctx.repositorio.proximoId(), turmaId, periodoId, professorResponsavel, INICIO, FIM, 6.0, 75.0);
        ctx.repositorio.salvar(diario);
    }

    @Quando("o professor adiciona uma avaliação com prazo dentro do período")
    public void adiciona_avaliacao_prazo_valido() {
        var avalId = ctx.repositorio.proximaAvaliacaoId();
        try {
            diario.adicionarAvaliacao(avalId, "Prova 1", 40.0, LocalDate.of(2025, 5, 20));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a avaliação é adicionada ao diário")
    public void avaliacao_adicionada() {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        assertEquals(1, diario.getAvaliacoes().size());
    }

    @Quando("o professor adiciona uma avaliação com prazo fora do período")
    public void adiciona_avaliacao_prazo_invalido() {
        var avalId = ctx.repositorio.proximaAvaliacaoId();
        try {
            diario.adicionarAvaliacao(avalId, "Prova 1", 40.0, LocalDate.of(2025, 9, 1));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o professor adiciona avaliações cuja soma dos pesos ultrapassa 100 por cento")
    public void adiciona_avaliacoes_peso_excedente() {
        var aval1Id = ctx.repositorio.proximaAvaliacaoId();
        diario.adicionarAvaliacao(aval1Id, "Prova 1", 70.0, LocalDate.of(2025, 5, 1));
        var aval2Id = ctx.repositorio.proximaAvaliacaoId();
        try {
            diario.adicionarAvaliacao(aval2Id, "Prova 2", 40.0, LocalDate.of(2025, 6, 1));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
