package school.cesar.acadlab.dominio.gestaopedagogica.diario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.gestaopedagogica.GestaoPedagogicaFuncionalidade;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.apuracao.RegimeApuracao;

public class ApuracaoResultadoFuncionalidade {

    private final GestaoPedagogicaFuncionalidade ctx;

    private static final LocalDate INICIO = LocalDate.of(2025, 2, 1);
    private static final LocalDate FIM = LocalDate.of(2025, 7, 31);
    private static final LocalDate DENTRO = LocalDate.of(2025, 3, 10);

    private final TurmaId turmaId = new TurmaId(40);
    private final PeriodoLetivoId periodoId = new PeriodoLetivoId(40);
    private final ProfessorId professor = new ProfessorId(40);
    private final EstudanteId estudante = new EstudanteId(40);

    private DiarioTurma diario;
    private AvaliacaoId p1;
    private AvaliacaoId p2;

    public ApuracaoResultadoFuncionalidade(GestaoPedagogicaFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um diário com avaliações de pesos {int} e {int} e o estudante com notas {int} e {int}")
    public void diario_com_avaliacoes_e_notas(int peso1, int peso2, int nota1, int nota2) {
        diario = new DiarioTurma(ctx.repositorio.proximoId(), turmaId, periodoId, professor,
                INICIO, FIM, 6.0, 75.0);
        p1 = ctx.repositorio.proximaAvaliacaoId();
        diario.adicionarAvaliacao(p1, "P1", peso1, DENTRO);
        p2 = ctx.repositorio.proximaAvaliacaoId();
        diario.adicionarAvaliacao(p2, "P2", peso2, DENTRO);

        diario.adicionarEstudanteAtivo(estudante);
        var aulaId = ctx.repositorio.proximoAulaId();
        diario.registrarAula(aulaId, professor, DENTRO, "Conteúdo");
        diario.registrarFrequencia(professor, aulaId, estudante, true);
        diario.lancarNota(estudante, p1, nota1);
        diario.lancarNota(estudante, p2, nota2);
    }

    @Quando("o professor fecha o resultado pelo regime {string}")
    public void professor_fecha_pelo_regime(String regime) {
        try {
            diario.fecharResultado(estudante, RegimeApuracao.valueOf(regime).apuracao());
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a situação final do estudante é {string}")
    public void situacao_final_do_estudante(String situacaoEsperada) {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        var resultado = diario.getResultados().stream()
                .filter(r -> r.getEstudanteId().equals(estudante))
                .findFirst().orElseThrow();
        assertEquals(SituacaoResultado.valueOf(situacaoEsperada), resultado.getSituacao());
    }
}
