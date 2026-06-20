package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.E;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.gestaofinanceira.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ContestarCobrancaFuncionalidade {
    private final GestaoFinanceiraFuncionalidade ctx;
    private CobrancaId cobrancaId;

    public ContestarCobrancaFuncionalidade(GestaoFinanceiraFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma cobrança aberta para o estudante {int} no contrato {int}")
    public void cobrancaAbertaParaEstudante(int estudanteId, int contratoId) {
        ctx.verificadorMatricula.setMatricula(true);
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(estudanteId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
    }

    @E("o estudante {int} já contestou a cobrança anteriormente")
    public void estudanteJaContestouCobranca(int estudanteId) {
        ctx.servico.contestar(cobrancaId, new EstudanteId(estudanteId), "primeira contestação");
    }

    @Quando("o estudante {int} contesta a cobrança com justificativa {string}")
    public void estudanteContestaCobranca(int estudanteId, String justificativa) {
        ctx.servico.contestar(cobrancaId, new EstudanteId(estudanteId), justificativa);
    }

    @Quando("o estudante {int} tenta contestar a cobrança do estudante {int}")
    public void estudanteTentaContestarCobrancaDeOutro(int estudanteId, int donoId) {
        try {
            ctx.servico.contestar(cobrancaId, new EstudanteId(estudanteId), "tentativa indevida");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o estudante {int} tenta contestar a cobrança novamente")
    public void estudanteTentaContestarNovamente(int estudanteId) {
        try {
            ctx.servico.contestar(cobrancaId, new EstudanteId(estudanteId), "segunda contestação");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a cobrança deve ter status CONTESTADA")
    public void cobrancaDeveTerStatusContestada() {
        Assertions.assertEquals(StatusCobranca.CONTESTADA, ctx.repositorio.obter(cobrancaId).getStatus());
    }
}
