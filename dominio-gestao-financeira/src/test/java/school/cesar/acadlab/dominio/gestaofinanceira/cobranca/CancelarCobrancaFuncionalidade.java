package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.gestaofinanceira.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CancelarCobrancaFuncionalidade {
    private final GestaoFinanceiraFuncionalidade ctx;
    private CobrancaId cobrancaId;

    public CancelarCobrancaFuncionalidade(GestaoFinanceiraFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma cobrança aberta para cancelamento no contrato {int}")
    public void cobrancaAbertaParaCancelamento(int contratoId) {
        ctx.verificadorMatricula.setMatricula(true);
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(900 + contratoId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
    }

    @Dado("uma cobrança paga para cancelamento no contrato {int}")
    public void cobrancaPagaParaCancelamento(int contratoId) {
        ctx.verificadorMatricula.setMatricula(true);
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(950 + contratoId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
        ctx.servico.registrarPagamento(cobrancaId, new BigDecimal("1500.00"), LocalDate.now(), "PAG-CANCEL-COBR");
    }

    @Quando("o operador cancela a cobrança com motivo {string}")
    public void operadorCancelaCobranca(String motivo) {
        ctx.servico.cancelarCobranca(cobrancaId, motivo);
    }

    @Quando("o operador tenta cancelar a cobrança paga")
    public void operadorTentaCancelarCobrancaPaga() {
        try {
            ctx.servico.cancelarCobranca(cobrancaId, "Matrícula cancelada");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a cobrança deve ter status CANCELADA")
    public void cobrancaDeveTerStatusCancelada() {
        Assertions.assertEquals(StatusCobranca.CANCELADA, ctx.repositorio.obter(cobrancaId).getStatus());
    }
}
