package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.E;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.gestaofinanceira.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class AplicarDescontoFuncionalidade {
    private final GestaoFinanceiraFuncionalidade ctx;
    private CobrancaId cobrancaId;

    public AplicarDescontoFuncionalidade(GestaoFinanceiraFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma cobrança aberta de {double} para o estudante {int}")
    public void cobrancaAbertaDeValor(double valor, int estudanteId) {
        ctx.verificadorMatricula.setMatricula(true);
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(estudanteId * 10), new EstudanteId(estudanteId),
                new PeriodoLetivoId(1), BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP),
                LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
    }

    @E("a autorização {string} é válida")
    public void autorizacaoEValida(String autorizacaoId) {
        ctx.verificadorAutorizacao.marcarValida(autorizacaoId);
    }

    @Quando("aplico um desconto de {int} por cento com autorização {string}")
    public void aplicoDesconto(int percentual, String autorizacaoId) {
        ctx.servico.aplicarDesconto(cobrancaId, new BigDecimal(percentual), autorizacaoId);
    }

    @Quando("tento aplicar um desconto de {int} por cento com autorização {string}")
    public void tentoAplicarDesconto(int percentual, String autorizacaoId) {
        try {
            ctx.servico.aplicarDesconto(cobrancaId, new BigDecimal(percentual), autorizacaoId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o valor atual da cobrança deve ser {double}")
    public void valorAtualDeveSerIgualA(double valor) {
        var cobranca = ctx.repositorio.obter(cobrancaId);
        Assertions.assertEquals(0,
                BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).compareTo(cobranca.getValorAtual()));
    }
}
