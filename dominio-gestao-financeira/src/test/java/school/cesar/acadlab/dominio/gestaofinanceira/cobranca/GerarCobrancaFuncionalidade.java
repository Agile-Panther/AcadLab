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

public class GerarCobrancaFuncionalidade {
    private final GestaoFinanceiraFuncionalidade ctx;
    private CobrancaId cobrancaId;

    public GerarCobrancaFuncionalidade(GestaoFinanceiraFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("o estudante {int} possui matrícula confirmada no período letivo {int}")
    public void estudantePossuiMatriculaConfirmada(int estudanteId, int periodoId) {
        ctx.verificadorMatricula.setMatricula(true);
    }

    @Dado("o estudante {int} não possui matrícula confirmada no período letivo {int}")
    public void estudanteNaoPossuiMatriculaConfirmada(int estudanteId, int periodoId) {
        ctx.verificadorMatricula.setMatricula(false);
    }

    @E("uma cobrança foi gerada para o estudante {int} no contrato {int}")
    public void cobrancaFoiGeradaParaEstudante(int estudanteId, int contratoId) {
        ctx.verificadorMatricula.setMatricula(true);
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(estudanteId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
    }

    @Quando("gero uma cobrança para o estudante {int} no contrato {int} com valor {double}")
    public void geroCobranca(int estudanteId, int contratoId, double valor) {
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(estudanteId),
                new PeriodoLetivoId(1), BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP),
                LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
    }

    @Quando("tento gerar uma cobrança para o estudante {int} no contrato {int} com valor {double}")
    public void tentoGerarCobranca(int estudanteId, int contratoId, double valor) {
        try {
            ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(estudanteId),
                    new PeriodoLetivoId(1), BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP),
                    LocalDate.of(2025, 2, 10));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("gero nova versão da cobrança com motivo {string} e valor {double}")
    public void geroNovaVersao(String motivo, double valor) {
        ctx.servico.gerarNovaVersao(cobrancaId, motivo,
                BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP));
    }

    @Entao("a cobrança deve ser gerada com status ABERTA")
    public void cobrancaDeveSerGeradaComStatusAberta() {
        Assertions.assertEquals(StatusCobranca.ABERTA, ctx.repositorio.obter(cobrancaId).getStatus());
    }

    @Entao("a cobrança deve estar na versão {int} com valor {double}")
    public void cobrancaDeveEstarNaVersao(int versao, double valor) {
        var cobranca = ctx.repositorio.obter(cobrancaId);
        Assertions.assertEquals(versao, cobranca.getVersao());
        Assertions.assertEquals(0, BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP)
                .compareTo(cobranca.getValorAtual()));
    }
}
