package school.cesar.acadlab.dominio.atividadescomplementares.atividade;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.atividadescomplementares.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SubmeterAtividadeFuncionalidade {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final AtividadesComplementaresFuncionalidade ctx;
    private AtividadeComplementar resultado;

    public SubmeterAtividadeFuncionalidade(AtividadesComplementaresFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Given("um estudante com vínculo ativo no período de realização da atividade")
    public void estudanteComVinculoAtivo() {
        ctx.verificadorVinculo.setVinculo(true);
    }

    @Given("um estudante sem vínculo ativo na data de realização")
    public void estudanteSemVinculoAtivo() {
        ctx.verificadorVinculo.setVinculo(false);
    }

    @Given("o certificado {string} ainda não foi utilizado")
    public void certificadoNaoUtilizado(String cert) {
        // por padrão o stub retorna false para qualquer cert
    }

    @Given("o certificado {string} já foi utilizado anteriormente")
    public void certificadoJaUtilizado(String cert) {
        ctx.verificadorCertificado.marcarUtilizado(cert);
    }

    @When("o estudante submete a atividade da categoria {int} com {int} horas realizada em {string} com certificado {string}")
    public void submeteAtividade(int categoriaId, int horas, String data, String cert) {
        resultado = ctx.servico.submeter(new EstudanteId(1), new CategoriaAtividadeId(categoriaId),
                horas, LocalDate.parse(data, FMT), cert, "Descrição da atividade");
    }

    @When("o estudante tenta submeter a atividade da categoria {int} com {int} horas realizada em {string} com certificado {string}")
    public void tentaSubmeterAtividade(int categoriaId, int horas, String data, String cert) {
        try {
            ctx.servico.submeter(new EstudanteId(1), new CategoriaAtividadeId(categoriaId),
                    horas, LocalDate.parse(data, FMT), cert, "Descrição da atividade");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Then("a atividade deve ser salva com status PENDENTE")
    public void atividadeDeveSerSalvaComStatusPendente() {
        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(StatusAtividade.PENDENTE, resultado.getStatus());
    }

    @Then("deve ser lançada uma exceção de vínculo inativo")
    public void deveSerLancadaExcecaoVinculo() {
        Assertions.assertNotNull(ctx.excecao);
        Assertions.assertInstanceOf(IllegalStateException.class, ctx.excecao);
    }

    @Then("deve ser lançada uma exceção de certificado duplicado")
    public void deveSerLancadaExcecaoCertificado() {
        Assertions.assertNotNull(ctx.excecao);
        Assertions.assertInstanceOf(IllegalStateException.class, ctx.excecao);
    }
}
