package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.gestaofinanceira.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class RegistrarPagamentoFuncionalidade {
    private final GestaoFinanceiraFuncionalidade ctx;
    private CobrancaId cobrancaId;

    public RegistrarPagamentoFuncionalidade(GestaoFinanceiraFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Given("uma cobrança aberta no sistema com valor {double} para o contrato {int}")
    public void cobrancaAbertaNoSistema(double valor, int contratoId) {
        ctx.verificadorMatricula.setMatricula(true);
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(100 + contratoId),
                new PeriodoLetivoId(1), BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP),
                LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
    }

    @When("registro o pagamento de {double} com referência {string}")
    public void registroPagamento(double valor, String referencia) {
        ctx.servico.registrarPagamento(cobrancaId,
                BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP), LocalDate.now(), referencia);
    }

    @Then("a cobrança deve ter status PAGA")
    public void cobrancaDeveTerStatusPaga() {
        Assertions.assertEquals(StatusCobranca.PAGA, ctx.repositorio.obter(cobrancaId).getStatus());
    }

    @Then("o pagamento deve estar com status CONFIRMADO")
    public void pagamentoDeveEstarComStatusConfirmado() {
        var pagamento = ctx.repositorio.obter(cobrancaId).getPagamento();
        Assertions.assertNotNull(pagamento);
        Assertions.assertEquals(StatusPagamento.CONFIRMADO, pagamento.getStatus());
    }
}
