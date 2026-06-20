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
        ctx.servico.resolverContestacao(cobrancaId, "Primeira resolução");
    }

    @Quando("o setor financeiro resolve a contestação com parecer {string}")
    public void setorFinanceiroResolveContestacao(String parecer) {
        ctx.servico.resolverContestacao(cobrancaId, parecer);
    }

    @Quando("o setor financeiro tenta resolver a contestação")
    public void setorFinanceiroTentaResolverContestacao() {
        try {
            ctx.servico.resolverContestacao(cobrancaId, "parecer");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o setor financeiro tenta resolver a contestação novamente")
    public void setorFinanceiroTentaResolverNovamente() {
        try {
            ctx.servico.resolverContestacao(cobrancaId, "novo parecer");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a contestação deve ter status RESOLVIDA")
    public void contestacaoDeveTerStatusResolvida() {
        var contestacao = ctx.repositorio.obter(cobrancaId).getContestacao();
        Assertions.assertNotNull(contestacao);
        Assertions.assertEquals(StatusContestacao.RESOLVIDA, contestacao.getStatus());
    }

    @E("a cobrança deve retornar ao status ABERTA após resolução")
    public void cobrancaDeveRetornarAoStatusAbertaAposResolucao() {
        Assertions.assertEquals(StatusCobranca.ABERTA, ctx.repositorio.obter(cobrancaId).getStatus());
    }
}
