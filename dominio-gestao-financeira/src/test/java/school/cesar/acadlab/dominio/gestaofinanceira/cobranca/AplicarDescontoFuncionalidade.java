package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.gestaofinanceira.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class AplicarDescontoFuncionalidade extends GestaoFinanceiraFuncionalidade {
    private CobrancaId cobrancaId;
    private Exception excecao;

    @Given("uma cobrança aberta de {double} para o estudante {int}")
    public void cobrancaAbertaDeValor(double valor, int estudanteId) {
        verificadorMatricula.setMatricula(true);
        var cobranca = servico.gerarCobranca(new ContratoId(estudanteId * 10), new EstudanteId(estudanteId),
                new PeriodoLetivoId(1), BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP),
                LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
    }

    @Given("a autorização {string} é válida")
    public void autorizacaoEValida(String autorizacaoId) {
        verificadorAutorizacao.marcarValida(autorizacaoId);
    }

    @When("aplico um desconto de {int} por cento com autorização {string}")
    public void aplicoDesconto(int percentual, String autorizacaoId) {
        servico.aplicarDesconto(cobrancaId, new BigDecimal(percentual), autorizacaoId);
    }

    @When("tento aplicar um desconto de {int} por cento com autorização {string}")
    public void tentoAplicarDesconto(int percentual, String autorizacaoId) {
        try {
            servico.aplicarDesconto(cobrancaId, new BigDecimal(percentual), autorizacaoId);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Then("o valor atual da cobrança deve ser {double}")
    public void valorAtualDeveSerIgualA(double valor) {
        var cobranca = repositorio.obter(cobrancaId);
        Assertions.assertEquals(0,
                BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).compareTo(cobranca.getValorAtual()));
    }

    @Then("deve ser lançada uma exceção de autorização inválida")
    public void deveSerLancadaExcecaoAutorizacaoInvalida() {
        Assertions.assertNotNull(excecao);
        Assertions.assertInstanceOf(IllegalStateException.class, excecao);
    }
}
