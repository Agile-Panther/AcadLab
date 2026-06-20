package school.cesar.acadlab.dominio.atividadescomplementares.atividade;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.atividadescomplementares.*;
import java.time.LocalDate;

public class AnalisarAtividadeFuncionalidade {
    private final AtividadesComplementaresFuncionalidade ctx;
    private AtividadeComplementarId atividadeId;

    public AnalisarAtividadeFuncionalidade(AtividadesComplementaresFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Given("uma atividade complementar pendente cadastrada para análise")
    public void atividadePendenteCadastrada() {
        ctx.verificadorVinculo.setVinculo(true);
        var atividade = ctx.servico.submeter(new EstudanteId(1), new CategoriaAtividadeId(1),
                40, LocalDate.of(2025, 3, 15), "CERT-ANALISE", "Curso para análise");
        atividadeId = atividade.getId();
    }

    @Given("o deferimento não excede o limite da categoria")
    public void deferimentoNaoExcedeLimite() {
        ctx.verificadorLimite.setExcede(false);
    }

    @Given("o deferimento excede o limite da categoria")
    public void deferimentoExcedeLimite() {
        ctx.verificadorLimite.setExcede(true);
    }

    @When("o coordenador defere a atividade com {int} horas aprovadas")
    public void coordenadorDefereAtividade(int horas) {
        ctx.servico.deferir(atividadeId, horas);
    }

    @When("o coordenador tenta deferir a atividade com {int} horas")
    public void coordenadorTentaDeferirAtividade(int horas) {
        try {
            ctx.servico.deferir(atividadeId, horas);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @When("o coordenador indefere a atividade com justificativa {string}")
    public void coordenadorIndefereAtividade(String justificativa) {
        ctx.servico.indeferir(atividadeId, justificativa);
    }

    @Then("a atividade deve ter status DEFERIDA com {int} horas aprovadas")
    public void atividadeDeveTerStatusDeferida(int horas) {
        var atividade = ctx.repositorio.obter(atividadeId);
        Assertions.assertEquals(StatusAtividade.DEFERIDA, atividade.getStatus());
        Assertions.assertEquals(horas, atividade.getHorasAprovadas());
    }

    @Then("deve ser lançada uma exceção de limite de categoria excedido")
    public void deveSerLancadaExcecaoLimite() {
        Assertions.assertNotNull(ctx.excecao);
        Assertions.assertInstanceOf(IllegalStateException.class, ctx.excecao);
    }

    @Then("a atividade deve ter status INDEFERIDA")
    public void atividadeDeveTerStatusIndeferida() {
        var atividade = ctx.repositorio.obter(atividadeId);
        Assertions.assertEquals(StatusAtividade.INDEFERIDA, atividade.getStatus());
    }
}
