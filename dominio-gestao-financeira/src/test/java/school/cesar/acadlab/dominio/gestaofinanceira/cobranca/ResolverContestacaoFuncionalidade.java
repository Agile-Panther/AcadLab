package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.E;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.gestaofinanceira.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ResolverContestacaoFuncionalidade {
    private final GestaoFinanceiraFuncionalidade ctx;
    private CobrancaId cobrancaId;

    public ResolverContestacaoFuncionalidade(GestaoFinanceiraFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma cobrança contestada pelo estudante {int} no contrato {int}")
    public void cobrancaContestadasPeloEstudante(int estudanteId, int contratoId) {
        ctx.verificadorMatricula.setMatricula(true);
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(estudanteId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
        ctx.servico.contestar(cobrancaId, new EstudanteId(estudanteId), "Valor diverge do contrato");
    }

    @Dado("uma cobrança aberta sem contestação para o contrato {int}")
    public void cobrancaAbertaSemContestacao(int contratoId) {
        ctx.verificadorMatricula.setMatricula(true);
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(700 + contratoId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
    }

    @Dado("uma cobrança com contestação já resolvida para o contrato {int}")
    public void cobrancaComContestacaoJaResolvida(int contratoId) {
        ctx.verificadorMatricula.setMatricula(true);
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(800 + contratoId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
        ctx.servico.contestar(cobrancaId, new EstudanteId(800 + contratoId), "Contestação inicial");
        ctx.servico.indeferirContestacao(cobrancaId, "Primeira resolução");
    }

    @Quando("o setor financeiro indefere a contestação com parecer {string}")
    public void indefere(String parecer) {
        ctx.servico.indeferirContestacao(cobrancaId, parecer);
    }

    @Quando("o setor financeiro defere a contestação com {int} por cento e parecer {string}")
    public void deferePercentual(int pct, String parecer) {
        ctx.servico.deferirContestacao(cobrancaId, ModoAjuste.PERCENTUAL, new BigDecimal(pct), parecer);
    }

    @Quando("o setor financeiro defere a contestação com o valor {string} e parecer {string}")
    public void defereValor(String valor, String parecer) {
        ctx.servico.deferirContestacao(cobrancaId, ModoAjuste.VALOR,
                new BigDecimal(valor).setScale(2, java.math.RoundingMode.HALF_UP), parecer);
    }

    @Quando("o setor financeiro tenta indeferir a contestação")
    public void tentaIndeferir() {
        try {
            ctx.servico.indeferirContestacao(cobrancaId, "parecer");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o setor financeiro tenta indeferir a contestação novamente")
    public void tentaIndeferirNovamente() {
        try {
            ctx.servico.indeferirContestacao(cobrancaId, "novo parecer");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a contestação deve ter status {string}")
    public void contestacaoDeveTerStatus(String status) {
        var contestacao = ctx.repositorio.obter(cobrancaId).getContestacao();
        Assertions.assertNotNull(contestacao);
        Assertions.assertEquals(StatusContestacao.valueOf(status), contestacao.getStatus());
    }

    @E("a cobrança deve retornar ao status ABERTA após resolução")
    public void cobrancaDeveRetornarAoStatusAbertaAposResolucao() {
        Assertions.assertEquals(StatusCobranca.ABERTA, ctx.repositorio.obter(cobrancaId).getStatus());
    }

    @E("o valor atual da cobrança permanece {string}")
    public void valorAtualPermanece(String valor) {
        Assertions.assertEquals(0, new BigDecimal(valor).setScale(2, java.math.RoundingMode.HALF_UP)
                .compareTo(ctx.repositorio.obter(cobrancaId).getValorAtual()));
    }
}
