package school.cesar.acadlab.dominio.atividadescomplementares.atividade;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.atividadescomplementares.*;
import java.time.LocalDate;

public class CancelarAtividadeFuncionalidade extends AtividadesComplementaresFuncionalidade {
    private AtividadeComplementarId atividadeId;
    private Exception excecao;

    @Given("uma atividade complementar com status pendente aguardando cancelamento")
    public void atividadePendenteAguardandoCancelamento() {
        verificadorVinculo.setVinculo(true);
        var atividade = servico.submeter(new EstudanteId(1), new CategoriaAtividadeId(1),
                40, LocalDate.of(2025, 3, 15), "CERT-CANCEL-PEND", "Curso para cancelar");
        atividadeId = atividade.getId();
    }

    @Given("uma atividade complementar com status deferida aguardando cancelamento")
    public void atividadeDeferidaAguardandoCancelamento() {
        verificadorVinculo.setVinculo(true);
        verificadorLimite.setExcede(false);
        var atividade = servico.submeter(new EstudanteId(1), new CategoriaAtividadeId(1),
                40, LocalDate.of(2025, 3, 15), "CERT-CANCEL-DEF", "Curso deferido");
        atividadeId = atividade.getId();
        servico.deferir(atividadeId, 30);
    }

    @When("o estudante solicita o cancelamento da submissão")
    public void estudanteSolicitaCancelamento() {
        servico.cancelar(atividadeId);
    }

    @When("o estudante tenta solicitar o cancelamento da submissão")
    public void estudanteTentaSolicitarCancelamento() {
        try {
            servico.cancelar(atividadeId);
        } catch (Exception e) {
            excecao = e;
        }
    }

    @Then("a atividade deve ter status CANCELADA")
    public void atividadeDeveTerStatusCancelada() {
        var atividade = repositorio.obter(atividadeId);
        Assertions.assertEquals(StatusAtividade.CANCELADA, atividade.getStatus());
    }

    @Then("deve ser lançada uma exceção de cancelamento inválido")
    public void deveSerLancadaExcecaoCancelamentoInvalido() {
        Assertions.assertNotNull(excecao);
        Assertions.assertInstanceOf(IllegalStateException.class, excecao);
    }
}
