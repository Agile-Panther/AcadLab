package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.E;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.evento.EventoObservador;
import school.cesar.acadlab.dominio.gestaofinanceira.*;
import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class AplicarDescontoComBolsaSteps {
    private final BolsaFuncionalidade bolsaCtx;
    private CobrancaServico cobrancaServico;
    private GestaoFinanceiraRepositorioTest cobrancaRepo;
    private CobrancaId cobrancaId;
    private String autorizacaoBolsa;
    private RuntimeException excecao;

    public AplicarDescontoComBolsaSteps(BolsaFuncionalidade bolsaCtx) {
        this.bolsaCtx = bolsaCtx;
    }

    private void inicializarCobrancaServico() {
        cobrancaRepo = new GestaoFinanceiraRepositorioTest();
        VerificadorMatriculaConfirmada matricula = (e, p) -> true;
        VerificadorAutorizacaoDesconto autorizacao = new AutorizacaoDescontoPorBolsa(bolsaCtx.repositorio);
        EventoBarramento barramento = new EventoBarramento() {
            @Override public <E> void adicionar(EventoObservador<E> o) {}
            @Override public <E> void postar(E ev) {}
        };
        cobrancaServico = new CobrancaServico(cobrancaRepo, matricula, autorizacao, barramento);
    }

    @Dado("uma bolsa {word} ativa de {int} por cento para o estudante {int}")
    public void bolsaAtiva(String tipo, int pct, int estudante) {
        var b = bolsaCtx.servico.conceder(new EstudanteId(estudante), TipoBolsa.valueOf(tipo),
                new BigDecimal(pct), LocalDate.of(2025, 12, 31));
        autorizacaoBolsa = String.valueOf(b.getId().valor());
    }

    @E("uma cobrança aberta de {double} para o estudante {int} contra o contrato {int}")
    public void cobrancaAberta(double valor, int estudante, int contrato) {
        if (cobrancaServico == null) inicializarCobrancaServico();
        var c = cobrancaServico.gerarCobranca(new ContratoId(contrato), new EstudanteId(estudante),
                new PeriodoLetivoId(1), BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP),
                LocalDate.of(2025, 2, 10));
        cobrancaId = c.getId();
    }

    @Quando("aplico o desconto da bolsa ativa na cobrança")
    public void aplicoDescontoBolsa() {
        cobrancaServico.aplicarDesconto(cobrancaId, new BigDecimal("10"), autorizacaoBolsa);
    }

    @Quando("tento aplicar um desconto de {int} por cento com a autorização {string}")
    public void tentoAplicar(int pct, String autorizacaoId) {
        try {
            cobrancaServico.aplicarDesconto(cobrancaId, new BigDecimal(pct), autorizacaoId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o valor atual da cobrança deve ser {double} reais")
    public void valorAtual(double valor) {
        Assertions.assertEquals(0, BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP)
                .compareTo(cobrancaRepo.obter(cobrancaId).getValorAtual()));
    }

    @Entao("o desconto é recusado por autorização inválida")
    public void recusado() {
        Assertions.assertNotNull(excecao);
        Assertions.assertTrue(excecao.getMessage().contains("autorização inválida"));
    }
}
