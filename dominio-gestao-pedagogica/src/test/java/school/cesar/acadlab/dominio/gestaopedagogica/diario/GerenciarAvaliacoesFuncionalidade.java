package school.cesar.acadlab.dominio.gestaopedagogica.diario;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.gestaopedagogica.GestaoPedagogicaFuncionalidade;

public class GerenciarAvaliacoesFuncionalidade extends GestaoPedagogicaFuncionalidade {

    private static final LocalDate INICIO = LocalDate.of(2025, 2, 1);
    private static final LocalDate FIM = LocalDate.of(2025, 7, 31);

    private final TurmaId turmaId = new TurmaId(20);
    private final PeriodoLetivoId periodoId = new PeriodoLetivoId(20);
    private final ProfessorId professorResponsavel = new ProfessorId(20);

    private DiarioTurma diario;
    private RuntimeException excecao;

    @Dado("um diário de turma vazio para gerenciamento de avaliações")
    public void diario_vazio_para_avaliacoes() {
        diario = new DiarioTurma(repositorio.proximoId(), turmaId, periodoId, professorResponsavel, INICIO, FIM, 6.0, 75.0);
        repositorio.salvar(diario);
    }

    @Quando("o professor adiciona uma avaliação com prazo dentro do período")
    public void adiciona_avaliacao_prazo_valido() {
        var avalId = repositorio.proximaAvaliacaoId();
        try {
            diario.adicionarAvaliacao(avalId, "Prova 1", 40.0, LocalDate.of(2025, 5, 20));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("a avaliação é adicionada ao diário")
    public void avaliacao_adicionada() {
        assertNull(excecao, "Não deveria ter lançado exceção");
        assertEquals(1, diario.getAvaliacoes().size());
    }

    @Quando("o professor adiciona uma avaliação com prazo fora do período")
    public void adiciona_avaliacao_prazo_invalido() {
        var avalId = repositorio.proximaAvaliacaoId();
        try {
            diario.adicionarAvaliacao(avalId, "Prova 1", 40.0, LocalDate.of(2025, 9, 1));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema rejeita a avaliação informando RN-6")
    public void sistema_rejeita_rn6() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-6"));
    }

    @Quando("o professor adiciona avaliações cuja soma dos pesos ultrapassa 100 por cento")
    public void adiciona_avaliacoes_peso_excedente() {
        var aval1Id = repositorio.proximaAvaliacaoId();
        diario.adicionarAvaliacao(aval1Id, "Prova 1", 70.0, LocalDate.of(2025, 5, 1));
        var aval2Id = repositorio.proximaAvaliacaoId();
        try {
            diario.adicionarAvaliacao(aval2Id, "Prova 2", 40.0, LocalDate.of(2025, 6, 1));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema rejeita a segunda avaliação informando RN-5")
    public void sistema_rejeita_rn5() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-5"));
    }
}
