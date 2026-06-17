package school.cesar.acadlab.dominio.estagios.estagio;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import school.cesar.acadlab.dominio.estagios.EstagiosFuncionalidade;
import school.cesar.acadlab.dominio.estagios.oportunidade.CoordenadorId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EmpresaId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EstudanteId;

public class SubmeterRelatorioFuncionalidade extends EstagiosFuncionalidade {

    @Given("um estágio ativo para o estudante de id {int}")
    public void um_estagio_ativo(int estudanteId) {
        criarEstagioEmAndamento(estudanteId);
    }

    @When("o estudante submete o relatório número {int} com descrição {string}")
    public void o_estudante_submete_relatorio(int numero, String descricao) {
        servico.submeterRelatorio(estagioId, numero, descricao);
    }

    @Then("o estágio possui {int} relatório com status PENDENTE")
    public void o_estagio_possui_relatorio_pendente(int quantidade) {
        var estagio = estagioRepositorio.buscarPorId(estagioId).orElseThrow();
        assertEquals(quantidade, estagio.getRelatorios().size());
        assertEquals(StatusRelatorio.PENDENTE, estagio.getRelatorios().get(0).getStatus());
    }

    @Given("um estágio com relatório número {int} já submetido")
    public void um_estagio_com_relatorio_submetido(int numero) {
        var oportunidadeId = servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        servico.candidatar(oportunidadeId, new EstudanteId(20));
        servico.encaminhar(oportunidadeId, new CoordenadorId(30));
        estagioId = servico.confirmar(oportunidadeId, new EmpresaId(10));
        servico.submeterRelatorio(estagioId, numero, "Primeiro relatório");
    }

    @When("o estudante tenta submeter novamente o relatório número {int}")
    public void o_estudante_tenta_submeter_duplicado(int numero) {
        try {
            servico.submeterRelatorio(estagioId, numero, "Tentativa duplicada");
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o sistema rejeita a submissão com mensagem sobre RN-8")
    public void sistema_rejeita_submissao_rn8() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-8"));
    }

    @Given("um estágio com relatório número {int} pendente")
    public void um_estagio_com_relatorio_pendente(int numero) {
        var oportunidadeId = servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        servico.candidatar(oportunidadeId, new EstudanteId(20));
        servico.encaminhar(oportunidadeId, new CoordenadorId(30));
        estagioId = servico.confirmar(oportunidadeId, new EmpresaId(10));
        servico.submeterRelatorio(estagioId, numero, "Relatório mensal");
    }

    @When("o coordenador aprova o relatório número {int}")
    public void o_coordenador_aprova_relatorio(int numero) {
        servico.avaliarRelatorio(estagioId, numero, StatusRelatorio.APROVADO);
    }

    @Then("o relatório número {int} possui status APROVADO")
    public void o_relatorio_possui_status_aprovado(int numero) {
        var estagio = estagioRepositorio.buscarPorId(estagioId).orElseThrow();
        var relatorio = estagio.getRelatorios().stream()
                .filter(r -> r.getNumero() == numero)
                .findFirst().orElseThrow();
        assertEquals(StatusRelatorio.APROVADO, relatorio.getStatus());
    }
}
