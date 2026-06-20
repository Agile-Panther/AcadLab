package school.cesar.acadlab.dominio.atividadescomplementares.atividade;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.atividadescomplementares.*;
import java.time.LocalDate;

public class CancelarAtividadeFuncionalidade {
    private final AtividadesComplementaresFuncionalidade ctx;
    private AtividadeComplementarId atividadeId;

    public CancelarAtividadeFuncionalidade(AtividadesComplementaresFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma atividade complementar com status pendente aguardando cancelamento")
    public void atividadePendenteAguardandoCancelamento() {
        ctx.verificadorVinculo.setVinculo(true);
        var atividade = ctx.servico.submeter(new EstudanteId(1), new CategoriaAtividadeId(1),
                40, LocalDate.of(2025, 3, 15), "CERT-CANCEL-PEND", "Curso para cancelar");
        atividadeId = atividade.getId();
    }

    @Dado("uma atividade complementar com status deferida aguardando cancelamento")
    public void atividadeDeferidaAguardandoCancelamento() {
        ctx.verificadorVinculo.setVinculo(true);
        ctx.verificadorLimite.setExcede(false);
        var atividade = ctx.servico.submeter(new EstudanteId(1), new CategoriaAtividadeId(1),
                40, LocalDate.of(2025, 3, 15), "CERT-CANCEL-DEF", "Curso deferido");
        atividadeId = atividade.getId();
        ctx.servico.deferir(atividadeId, 30);
    }

    @Quando("o estudante solicita o cancelamento da submissão")
    public void estudanteSolicitaCancelamento() {
        ctx.servico.cancelar(atividadeId);
    }

    @Quando("o estudante tenta solicitar o cancelamento da submissão")
    public void estudanteTentaSolicitarCancelamento() {
        try {
            ctx.servico.cancelar(atividadeId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a atividade deve ter status CANCELADA")
    public void atividadeDeveTerStatusCancelada() {
        var atividade = ctx.repositorio.obter(atividadeId);
        Assertions.assertEquals(StatusAtividade.CANCELADA, atividade.getStatus());
    }
}
