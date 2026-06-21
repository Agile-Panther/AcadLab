package school.cesar.acadlab.dominio.atividadescomplementares.atividade;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.atividadescomplementares.*;
import java.time.LocalDate;

public class SolicitarRevisaoFuncionalidade {
    private final AtividadesComplementaresFuncionalidade ctx;
    private AtividadeComplementarId atividadeId;

    public SolicitarRevisaoFuncionalidade(AtividadesComplementaresFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma atividade complementar no estado indeferida")
    public void atividadeNoEstadoIndeferida() {
        ctx.verificadorVinculo.setVinculo(true);
        ctx.verificadorLimite.setExcede(false);
        var atividade = ctx.servico.submeter(new EstudanteId(1), new CategoriaAtividadeId(1),
                40, LocalDate.of(2025, 3, 15), "CERT-REVISAO", "Curso para revisão");
        atividadeId = atividade.getId();
        ctx.servico.indeferir(atividadeId, "Documentação insuficiente");
    }

    @Dado("a atividade não foi contabilizada na integralização curricular")
    public void atividadeNaoContabilizada() {
        ctx.verificadorContabilizacao.setContabilizada(false);
    }

    @Dado("a atividade já foi contabilizada na integralização curricular")
    public void atividadeJaContabilizada() {
        ctx.verificadorContabilizacao.setContabilizada(true);
    }

    @Quando("o estudante solicita revisão com justificativa {string}")
    public void estudanteSolicitaRevisao(String justificativa) {
        ctx.servico.solicitarRevisao(atividadeId, justificativa);
    }

    @Quando("o estudante tenta solicitar revisão da atividade")
    public void estudanteTentaSolicitarRevisao() {
        try {
            ctx.servico.solicitarRevisao(atividadeId, "justificativa");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a atividade deve ter status REVISAO_SOLICITADA")
    public void atividadeDeveTerStatusRevisaoSolicitada() {
        var atividade = ctx.repositorio.obter(atividadeId);
        Assertions.assertEquals(StatusAtividade.REVISAO_SOLICITADA, atividade.getStatus());
    }
}
