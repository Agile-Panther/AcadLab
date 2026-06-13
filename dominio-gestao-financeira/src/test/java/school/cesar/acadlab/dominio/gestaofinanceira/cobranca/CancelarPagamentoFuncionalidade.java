package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.gestaofinanceira.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CancelarPagamentoFuncionalidade extends GestaoFinanceiraFuncionalidade {
    private CobrancaId cobrancaId;
    private Exception excecao;

    @Given("uma cobrança confirmada com referência {string} para o contrato {int}")
    public void cobrancaConfirmadaComReferenciaParaContrato(String referencia, int contratoId) {
        verificadorMatricula.setMatricula(true);
        var cobranca = servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(500 + contratoId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
        servico.registrarPagamento(cobrancaId, new BigDecimal("1500.00"), LocalDate.now(), referencia);
    }

    @Given("uma cobrança com pagamento já cancelado no contrato {int}")
    public void cobrancaComPagamentoJaCancelado(int contratoId) {
        verificadorMatricula.setMatricula(true);
        var cobranca = servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(600 + contratoId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
        servico.registrarPagamento(cobrancaId, new BigDecimal("1500.00"), LocalDate.now(), "PAG-JA-CANCEL");
        servico.cancelarPagamento(cobrancaId, "primeiro cancelamento", "operador");
    }

    @When("o operador cancela o pagamento com justificativa {string}")
    public void operadorCancelaPagamento(String justificativa) {
        servico.cancelarPagamento(cobrancaId, justificativa, "operador@cesar.school");
    }

    @When("o operador tenta cancelar o pagamento já cancelado")
    public void operadorTentaCancelarPagamentoJaCancelado() {
        try {
            servico.cancelarPagamento(cobrancaId, "segundo cancelamento", "operador@cesar.school");
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Then("o pagamento deve ter status CANCELADO")
    public void pagamentoDeveTerStatusCancelado() {
        Assertions.assertEquals(StatusPagamento.CANCELADO,
                repositorio.obter(cobrancaId).getPagamento().getStatus());
    }

    @Then("a cobrança deve voltar para o status ABERTA")
    public void cobrancaDeveVoltarParaStatusAberta() {
        Assertions.assertEquals(StatusCobranca.ABERTA, repositorio.obter(cobrancaId).getStatus());
    }

    @Then("deve ser lançada uma exceção de pagamento não cancelável")
    public void deveSerLancadaExcecaoPagamentoNaoCancelavel() {
        Assertions.assertNotNull(excecao);
        Assertions.assertInstanceOf(IllegalStateException.class, excecao);
    }
}
