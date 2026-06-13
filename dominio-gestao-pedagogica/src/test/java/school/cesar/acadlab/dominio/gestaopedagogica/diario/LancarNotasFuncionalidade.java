package school.cesar.acadlab.dominio.gestaopedagogica.diario;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.gestaopedagogica.GestaoPedagogicaFuncionalidade;

public class LancarNotasFuncionalidade extends GestaoPedagogicaFuncionalidade {

    private static final LocalDate INICIO = LocalDate.of(2025, 2, 1);
    private static final LocalDate FIM = LocalDate.of(2025, 7, 31);

    private final TurmaId turmaId = new TurmaId(30);
    private final PeriodoLetivoId periodoId = new PeriodoLetivoId(30);
    private final ProfessorId professor = new ProfessorId(30);
    private final EstudanteId estudante = new EstudanteId(30);

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

    @Dado("um diário com estudante matriculado com frequência suficiente e nota aprovada")
    public void diario_estudante_nota_aprovada() {
        setupDiarioComEstudante();
        diario.lancarNota(estudante, avalId, 8.0);
    }

    @Quando("o professor fecha o resultado do estudante")
    public void professor_fecha_resultado() {
        try {
            diario.fecharResultado(estudante);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o resultado do estudante é marcado como aprovado")
    public void resultado_aprovado() {
        assertNull(excecao, "Não deveria ter lançado exceção");
        var resultado = diario.getResultados().stream()
                .filter(r -> r.getEstudanteId().equals(estudante))
                .findFirst().orElseThrow();
        assertEquals(SituacaoResultado.APROVADO, resultado.getSituacao());
    }

    @Dado("um diário com estudante matriculado com frequência suficiente e nota reprovada")
    public void diario_estudante_nota_reprovada() {
        setupDiarioComEstudante();
        diario.lancarNota(estudante, avalId, 2.0);
    }

    @Quando("o professor fecha o resultado reprovado do estudante")
    public void professor_fecha_resultado_reprovado() {
        try {
            diario.fecharResultado(estudante);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o resultado do estudante é marcado como reprovado por nota")
    public void resultado_reprovado_nota() {
        assertNull(excecao, "Não deveria ter lançado exceção");
        var resultado = diario.getResultados().stream()
                .filter(r -> r.getEstudanteId().equals(estudante))
                .findFirst().orElseThrow();
        assertEquals(SituacaoResultado.REPROVADO_NOTA, resultado.getSituacao());
    }

    @Dado("um diário com estudante cujo resultado já está fechado")
    public void diario_resultado_ja_fechado() {
        setupDiarioComEstudante();
        diario.lancarNota(estudante, avalId, 7.0);
        diario.fecharResultado(estudante);
    }

    @Quando("o professor tenta alterar a nota após o fechamento")
    public void professor_tenta_alterar_nota() {
        try {
            diario.lancarNota(estudante, avalId, 9.0);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema rejeita a alteração informando RN-8")
    public void sistema_rejeita_rn8() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-8"));
    }

    @Dado("um diário com estudante matriculado para revisão de nota")
    public void diario_para_revisao() {
        diario = new DiarioTurma(repositorio.proximoId(), turmaId, periodoId, professor, INICIO, FIM, 6.0, 75.0);
        diario.adicionarEstudanteAtivo(estudante);
        repositorio.salvar(diario);
    }

    @Quando("o estudante solicita revisão de nota dentro da janela permitida")
    public void solicita_revisao_dentro_janela() {
        try {
            diario.solicitarRevisaoNota(estudante, LocalDate.of(2025, 8, 5), LocalDate.of(2025, 8, 15));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("a solicitação de revisão é registrada para o estudante")
    public void revisao_registrada() {
        assertNull(excecao, "Não deveria ter lançado exceção");
        var resultado = diario.getResultados().stream()
                .filter(r -> r.getEstudanteId().equals(estudante))
                .findFirst().orElseThrow();
        assertTrue(resultado.isRevisaoSolicitada());
    }

    @Quando("o estudante solicita revisão de nota após o fim da janela")
    public void solicita_revisao_fora_janela() {
        try {
            diario.solicitarRevisaoNota(estudante, LocalDate.of(2025, 8, 20), LocalDate.of(2025, 8, 15));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema rejeita a revisão informando RN-9")
    public void sistema_rejeita_rn9() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-9"));
    }
}
