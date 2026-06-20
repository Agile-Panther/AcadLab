package school.cesar.acadlab.dominio.atividadescomplementares.atividade;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.E;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.atividadescomplementares.*;
import java.time.LocalDate;
import java.util.Map;

public class VisualizarSaldoFuncionalidade {
    private final AtividadesComplementaresFuncionalidade ctx;
    private Map<CategoriaAtividadeId, Integer> saldo;

    public VisualizarSaldoFuncionalidade(AtividadesComplementaresFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("o estudante {int} possui uma atividade DEFERIDA na categoria {int} com {int} horas aprovadas")
    public void estudantePossuiAtividadeDeferida(int estudanteId, int categoriaId, int horas) {
        ctx.verificadorVinculo.setVinculo(true);
        ctx.verificadorLimite.setExcede(false);
        var atividade = ctx.servico.submeter(new EstudanteId(estudanteId), new CategoriaAtividadeId(categoriaId),
                horas, LocalDate.of(2025, 3, 15), "CERT-SALDO-" + estudanteId + "-" + categoriaId + "-DEF", "Curso");
        ctx.servico.deferir(atividade.getId(), horas);
    }

    @E("o estudante {int} possui uma atividade PENDENTE na categoria {int} com {int} horas submetidas")
    public void estudantePossuiAtividadePendente(int estudanteId, int categoriaId, int horas) {
        ctx.verificadorVinculo.setVinculo(true);
        ctx.servico.submeter(new EstudanteId(estudanteId), new CategoriaAtividadeId(categoriaId),
                horas, LocalDate.of(2025, 3, 15), "CERT-SALDO-" + estudanteId + "-" + categoriaId + "-PEND", "Curso");
    }

    @E("o estudante {int} possui uma atividade INDEFERIDA na categoria {int} com {int} horas submetidas")
    public void estudantePossuiAtividadeIndeferida(int estudanteId, int categoriaId, int horas) {
        ctx.verificadorVinculo.setVinculo(true);
        var atividade = ctx.servico.submeter(new EstudanteId(estudanteId), new CategoriaAtividadeId(categoriaId),
                horas, LocalDate.of(2025, 3, 15), "CERT-SALDO-" + estudanteId + "-" + categoriaId + "-IND", "Curso");
        ctx.servico.indeferir(atividade.getId(), "motivo");
    }

    @Quando("consulto o saldo de horas do estudante {int}")
    public void consultoSaldoHoras(int estudanteId) {
        saldo = ctx.servico.calcularSaldoHoras(new EstudanteId(estudanteId));
    }

    @Entao("o saldo da categoria {int} deve ser {int} horas")
    public void saldoDaCategoriaDeveSer(int categoriaId, int horas) {
        Assertions.assertEquals(horas, saldo.getOrDefault(new CategoriaAtividadeId(categoriaId), 0));
    }

    @E("a categoria {int} não deve aparecer no saldo")
    public void categoriaNaoDeveAparecerNoSaldo(int categoriaId) {
        Assertions.assertFalse(saldo.containsKey(new CategoriaAtividadeId(categoriaId)));
    }
}
