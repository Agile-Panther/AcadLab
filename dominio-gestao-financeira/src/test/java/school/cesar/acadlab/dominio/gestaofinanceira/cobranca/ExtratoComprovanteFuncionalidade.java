package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.gestaofinanceira.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ExtratoComprovanteFuncionalidade {
    private final GestaoFinanceiraFuncionalidade ctx;
    private CobrancaId cobrancaId;
    private List<Cobranca> extrato;
    private Pagamento comprovante;

    public ExtratoComprovanteFuncionalidade(GestaoFinanceiraFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Given("o contrato {int} possui {int} cobranças geradas")
    public void contratoPossuiCobrancasGeradas(int contratoId, int quantidade) {
        ctx.verificadorMatricula.setMatricula(true);
        for (int i = 0; i < quantidade; i++) {
            ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(200 + contratoId + i),
                    new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        }
    }

    @Given("uma cobrança paga com referência {string} para o contrato {int}")
    public void cobrancaPagaComReferencia(String referencia, int contratoId) {
        ctx.verificadorMatricula.setMatricula(true);
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(300 + contratoId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
        ctx.servico.registrarPagamento(cobrancaId, new BigDecimal("1500.00"), LocalDate.now(), referencia);
    }

    @Given("uma cobrança aberta sem pagamento para o contrato {int}")
    public void cobrancaAbertaSemPagamento(int contratoId) {
        ctx.verificadorMatricula.setMatricula(true);
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(400 + contratoId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
    }

    @When("consulto o extrato do contrato {int}")
    public void consultoExtrato(int contratoId) {
        extrato = ctx.servico.consultarExtrato(new ContratoId(contratoId));
    }

    @When("solicito o comprovante da cobrança")
    public void solicitoComprovante() {
        comprovante = ctx.servico.emitirComprovante(cobrancaId);
    }

    @When("solicito o comprovante da cobrança sem pagamento")
    public void solicitoComprovanteSemPagamento() {
        try {
            ctx.servico.emitirComprovante(cobrancaId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Then("o extrato deve conter {int} cobranças")
    public void extratoDeveConterCobrancas(int quantidade) {
        Assertions.assertEquals(quantidade, extrato.size());
    }

    @Then("o comprovante deve conter a referência {string}")
    public void comprovanteDeveConterReferencia(String referencia) {
        Assertions.assertNotNull(comprovante);
        Assertions.assertEquals(referencia, comprovante.getReferencia());
    }

    @Then("deve ser lançada uma exceção de comprovante indisponível")
    public void deveSerLancadaExcecaoComprovanteIndisponivel() {
        Assertions.assertNotNull(ctx.excecao);
        Assertions.assertInstanceOf(IllegalStateException.class, ctx.excecao);
    }
}
