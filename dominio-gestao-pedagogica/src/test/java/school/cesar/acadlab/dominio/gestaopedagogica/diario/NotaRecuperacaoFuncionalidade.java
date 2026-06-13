package school.cesar.acadlab.dominio.gestaopedagogica.diario;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.gestaopedagogica.GestaoPedagogicaFuncionalidade;

public class NotaRecuperacaoFuncionalidade extends GestaoPedagogicaFuncionalidade {

    private static final LocalDate INICIO = LocalDate.of(2025, 2, 1);
    private static final LocalDate FIM = LocalDate.of(2025, 7, 31);

    private final TurmaId turmaId = new TurmaId(40);
    private final PeriodoLetivoId periodoId = new PeriodoLetivoId(40);
    private final ProfessorId professor = new ProfessorId(40);
    private final EstudanteId estudante = new EstudanteId(40);

    private DiarioTurma diario;
    private AvaliacaoId avalId;
    private RuntimeException excecao;

    private void setupDiarioComEstudante() {
        diario = new DiarioTurma(repositorio.proximoId(), turmaId, periodoId, professor, INICIO, FIM, 6.0, 75.0);
        avalId = repositorio.proximaAvaliacaoId();
        diario.adicionarAvaliacao(avalId, "Prova Final", 100.0, LocalDate.of(2025, 6, 15));
        diario.adicionarEstudanteAtivo(estudante);
        var aulaId = repositorio.proximoAulaId();
        diario.registrarAula(aulaId, professor, LocalDate.of(2025, 3, 10), "Conteúdo");
        diario.registrarFrequencia(professor, aulaId, estudante, true);
        repositorio.salvar(diario);
    }

    @Dado("um diário com estudante em situação de recuperação")
    public void diario_estudante_recuperacao() {
        setupDiarioComEstudante();
        // nota entre 60% e 100% da média mínima (6.0): entre 3.6 e 6.0
        diario.lancarNota(estudante, avalId, 4.5);
        diario.fecharResultado(estudante);
        var resultado = diario.getResultados().stream()
                .filter(r -> r.getEstudanteId().equals(estudante))
                .findFirst().orElseThrow();
        assertEquals(SituacaoResultado.RECUPERACAO, resultado.getSituacao());
    }

    @Quando("o professor registra nota de recuperação aprovada dentro do período")
    public void registra_recuperacao_aprovada() {
        try {
            diario.lancarNotaRecuperacao(estudante, 7.0, LocalDate.of(2025, 7, 20));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o resultado do estudante é atualizado para aprovado após recuperação")
    public void resultado_aprovado_recuperacao() {
        assertNull(excecao, "Não deveria ter lançado exceção");
        var resultado = diario.getResultados().stream()
                .filter(r -> r.getEstudanteId().equals(estudante))
                .findFirst().orElseThrow();
        assertEquals(SituacaoResultado.APROVADO, resultado.getSituacao());
    }

    @Dado("um diário com estudante aprovado sem necessidade de recuperação")
    public void diario_estudante_aprovado() {
        setupDiarioComEstudante();
        diario.lancarNota(estudante, avalId, 9.0);
        diario.fecharResultado(estudante);
        var resultado = diario.getResultados().stream()
                .filter(r -> r.getEstudanteId().equals(estudante))
                .findFirst().orElseThrow();
        assertEquals(SituacaoResultado.APROVADO, resultado.getSituacao());
    }

    @Quando("o professor tenta registrar nota de recuperação para estudante aprovado")
    public void tenta_recuperacao_aprovado() {
        try {
            diario.lancarNotaRecuperacao(estudante, 8.0, LocalDate.of(2025, 7, 20));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema rejeita a nota de recuperação informando RN-12")
    public void sistema_rejeita_rn12() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-12"));
    }

    @Dado("um diário com estudante que necessita nota de recuperação")
    public void diario_estudante_necessita_recuperacao() {
        diario = new DiarioTurma(repositorio.proximoId(), turmaId, periodoId, professor, INICIO, FIM, 6.0, 75.0);
        diario.adicionarEstudanteAtivo(estudante);
        repositorio.salvar(diario);
    }

    @Quando("o professor tenta registrar nota de recuperação após o fim do período")
    public void tenta_recuperacao_apos_periodo() {
        try {
            diario.lancarNotaRecuperacao(estudante, 7.0, LocalDate.of(2025, 8, 10));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema rejeita a nota de recuperação informando RN-13")
    public void sistema_rejeita_rn13() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-13"));
    }
}
