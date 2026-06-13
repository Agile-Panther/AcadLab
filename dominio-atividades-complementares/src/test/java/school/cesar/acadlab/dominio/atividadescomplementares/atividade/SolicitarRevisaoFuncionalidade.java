package school.cesar.acadlab.dominio.atividadescomplementares.atividade;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.atividadescomplementares.*;
import java.time.LocalDate;

public class SolicitarRevisaoFuncionalidade extends AtividadesComplementaresFuncionalidade {
    private AtividadeComplementarId atividadeId;
    private Exception excecao;

    @Given("uma atividade complementar no estado indeferida")
    public void atividadeNoEstadoIndeferida() {
        verificadorVinculo.setVinculo(true);
        verificadorLimite.setExcede(false);
        var atividade = servico.submeter(new EstudanteId(1), new CategoriaAtividadeId(1),
                40, LocalDate.of(2025, 3, 15), "CERT-REVISAO", "Curso para revisão");
        atividadeId = atividade.getId();
        servico.indeferir(atividadeId, "Documentação insuficiente");
    }

    @Given("a atividade não foi contabilizada na integralização curricular")
    public void atividadeNaoContabilizada() {
        verificadorContabilizacao.setContabilizada(false);
    }

    @Given("a atividade já foi contabilizada na integralização curricular")
    public void atividadeJaContabilizada() {
        verificadorContabilizacao.setContabilizada(true);
    }

    @When("o estudante solicita revisão com justificativa {string}")
    public void estudanteSolicitaRevisao(String justificativa) {
        servico.solicitarRevisao(atividadeId, justificativa);
    }

    @When("o estudante tenta solicitar revisão da atividade")
    public void estudanteTentaSolicitarRevisao() {
        try {
            servico.solicitarRevisao(atividadeId, "justificativa");
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Then("a atividade deve ter status REVISAO_SOLICITADA")
    public void atividadeDeveTerStatusRevisaoSolicitada() {
        var atividade = repositorio.obter(atividadeId);
        Assertions.assertEquals(StatusAtividade.REVISAO_SOLICITADA, atividade.getStatus());
    }

    @Then("deve ser lançada uma exceção de atividade já contabilizada")
    public void deveSerLancadaExcecaoContabilizada() {
        Assertions.assertNotNull(excecao);
        Assertions.assertInstanceOf(IllegalStateException.class, excecao);
    }
}
