package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.E;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CicloVidaBolsaSteps {
    private final BolsaFuncionalidade ctx;
    public CicloVidaBolsaSteps(BolsaFuncionalidade ctx) { this.ctx = ctx; }

    @Quando("concedo uma bolsa {word} de {int} por cento ao estudante {int} com validade {string}")
    public void concedo(String tipo, int pct, int estudante, String validade) {
        var b = ctx.servico.conceder(new EstudanteId(estudante), TipoBolsa.valueOf(tipo),
                new BigDecimal(pct), LocalDate.parse(validade));
        ctx.ultimaBolsa = b.getId();
    }

    @Dado("uma bolsa ATIVA do estudante {int}")
    public void bolsaAtiva(int estudante) {
        var b = ctx.servico.conceder(new EstudanteId(estudante), TipoBolsa.MERITO,
                new BigDecimal("50"), LocalDate.of(2025, 12, 31));
        ctx.ultimaBolsa = b.getId();
    }

    @E("a bolsa está suspensa")
    public void jaSuspensa() { ctx.servico.suspender(ctx.ultimaBolsa); }

    @Quando("suspendo a bolsa")
    public void suspendo() { ctx.servico.suspender(ctx.ultimaBolsa); }

    @Quando("reativo a bolsa")
    public void reativo() { ctx.servico.reativar(ctx.ultimaBolsa); }

    @Quando("solicito a renovação da bolsa")
    public void solicitoRenovacao() { ctx.servico.solicitarRenovacao(ctx.ultimaBolsa); }

    @Entao("a bolsa está com status {string}")
    public void statusEsperado(String status) {
        Assertions.assertEquals(StatusBolsa.valueOf(status), ctx.repositorio.obter(ctx.ultimaBolsa).getStatus());
    }
}
