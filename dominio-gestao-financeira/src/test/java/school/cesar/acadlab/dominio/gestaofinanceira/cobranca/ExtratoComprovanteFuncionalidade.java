package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
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

    @Dado("o contrato {int} possui {int} cobranças geradas")
    public void contratoPossuiCobrancasGeradas(int contratoId, int quantidade) {
        ctx.verificadorMatricula.setMatricula(true);
        for (int i = 0; i < quantidade; i++) {
            ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(200 + contratoId + i),
                    new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        }
    }

    @Dado("uma cobrança paga com referência {string} para o contrato {int}")
    public void cobrancaPagaComReferencia(String referencia, int contratoId) {
        ctx.verificadorMatricula.setMatricula(true);
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(300 + contratoId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
        ctx.servico.registrarPagamento(cobrancaId, new BigDecimal("1500.00"), LocalDate.now(), referencia);
    }

    @Dado("uma cobrança aberta sem pagamento para o contrato {int}")
    public void cobrancaAbertaSemPagamento(int contratoId) {
        ctx.verificadorMatricula.setMatricula(true);
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(400 + contratoId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
    }

    @Quando("consulto o extrato do contrato {int}")
    public void consultoExtrato(int contratoId) {
        extrato = ctx.servico.consultarExtrato(new ContratoId(contratoId));
    }

    @Quando("solicito o comprovante da cobrança")
    public void solicitoComprovante() {
        comprovante = ctx.servico.emitirComprovante(cobrancaId);
    }

    @Quando("solicito o comprovante da cobrança sem pagamento")
    public void solicitoComprovanteSemPagamento() {
        try {
            ctx.servico.emitirComprovante(cobrancaId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o extrato deve conter {int} cobranças")
    public void extratoDeveConterCobrancas(int quantidade) {
        Assertions.assertEquals(quantidade, extrato.size());
    }

    @Entao("o comprovante deve conter a referência {string}")
    public void comprovanteDeveConterReferencia(String referencia) {
        Assertions.assertNotNull(comprovante);
        Assertions.assertEquals(referencia, comprovante.getReferencia());
    }
}
