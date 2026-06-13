package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.gestaofinanceira.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class GerarCobrancaFuncionalidade extends GestaoFinanceiraFuncionalidade {
    private CobrancaId cobrancaId;
    private Exception excecao;

    @Given("o estudante {int} possui matrícula confirmada no período letivo {int}")
    public void estudantePossuiMatriculaConfirmada(int estudanteId, int periodoId) {
        verificadorMatricula.setMatricula(true);
    }

    @Given("o estudante {int} não possui matrícula confirmada no período letivo {int}")
    public void estudanteNaoPossuiMatriculaConfirmada(int estudanteId, int periodoId) {
        verificadorMatricula.setMatricula(false);
    }

    @Given("uma cobrança foi gerada para o estudante {int} no contrato {int}")
    public void cobrancaFoiGeradaParaEstudante(int estudanteId, int contratoId) {
        verificadorMatricula.setMatricula(true);
        var cobranca = servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(estudanteId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
    }

    @When("gero uma cobrança para o estudante {int} no contrato {int} com valor {double}")
    public void geroCobranca(int estudanteId, int contratoId, double valor) {
        var cobranca = servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(estudanteId),
                new PeriodoLetivoId(1), BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP),
                LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
    }

    @When("tento gerar uma cobrança para o estudante {int} no contrato {int} com valor {double}")
    public void tentoGerarCobranca(int estudanteId, int contratoId, double valor) {
        try {
            servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(estudanteId),
                    new PeriodoLetivoId(1), BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP),
                    LocalDate.of(2025, 2, 10));
        } catch (Exception e) {
            excecao = e;
        }
    }

    @When("gero nova versão da cobrança com motivo {string} e valor {double}")
    public void geroNovaVersao(String motivo, double valor) {
        servico.gerarNovaVersao(cobrancaId, motivo,
                BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP));
    }

    @Then("a cobrança deve ser gerada com status ABERTA")
    public void cobrancaDeveSerGeradaComStatusAberta() {
        Assertions.assertEquals(StatusCobranca.ABERTA, repositorio.obter(cobrancaId).getStatus());
    }

    @Then("deve ser lançada uma exceção de matrícula não confirmada")
    public void deveSerLancadaExcecaoMatriculaNaoConfirmada() {
        Assertions.assertNotNull(excecao);
        Assertions.assertInstanceOf(IllegalStateException.class, excecao);
    }

    @Then("a cobrança deve estar na versão {int} com valor {double}")
    public void cobrancaDeveEstarNaVersao(int versao, double valor) {
        var cobranca = repositorio.obter(cobrancaId);
        Assertions.assertEquals(versao, cobranca.getVersao());
        Assertions.assertEquals(0, BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP)
                .compareTo(cobranca.getValorAtual()));
    }
}
