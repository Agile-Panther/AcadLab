package school.cesar.acadlab.dominio.estagios.oportunidade;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import school.cesar.acadlab.dominio.estagios.EstagiosFuncionalidade;

public class CandidatarOportunidadeFuncionalidade extends EstagiosFuncionalidade {

    private OportunidadeId oportunidadeId;

    @Given("uma oportunidade de estágio aberta para a empresa de id {int} com descrição {string} e carga horária {int}")
    public void uma_oportunidade_de_estagio_aberta(int empresaId, String descricao, int cargaHoraria) {
        oportunidadeId = servico.cadastrarOportunidade(new EmpresaId(empresaId), descricao, cargaHoraria);
    }

    @When("o estudante de id {int} se candidata à oportunidade")
    public void o_estudante_se_candidata(int estudanteId) {
        servico.candidatar(oportunidadeId, new EstudanteId(estudanteId));
    }

    @Then("a oportunidade possui candidato com id {int}")
    public void a_oportunidade_possui_candidato_com_id(int estudanteId) {
        var oportunidade = oportunidadeRepositorio.buscarPorId(oportunidadeId).orElseThrow();
        assertEquals(new EstudanteId(estudanteId), oportunidade.getCandidato());
    }

    @Given("uma oportunidade de estágio já encaminhada")
    public void uma_oportunidade_ja_encaminhada() {
        oportunidadeId = servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        servico.candidatar(oportunidadeId, new EstudanteId(20));
        servico.encaminhar(oportunidadeId, new CoordenadorId(30));
    }

    @When("o estudante de id {int} tenta se candidatar à oportunidade encaminhada")
    public void o_estudante_tenta_se_candidatar_encaminhada(int estudanteId) {
        try {
            servico.candidatar(oportunidadeId, new EstudanteId(estudanteId));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o sistema rejeita a candidatura com mensagem sobre RN-1")
    public void sistema_rejeita_candidatura_rn1() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-1"));
    }

    @Given("uma oportunidade de estágio aberta com candidato de id {int}")
    public void uma_oportunidade_aberta_com_candidato(int estudanteId) {
        oportunidadeId = servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        servico.candidatar(oportunidadeId, new EstudanteId(estudanteId));
    }

    @When("o estudante de id {int} tenta se candidatar à oportunidade com candidato")
    public void o_estudante_tenta_se_candidatar_com_candidato(int estudanteId) {
        try {
            servico.candidatar(oportunidadeId, new EstudanteId(estudanteId));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o sistema rejeita a candidatura com mensagem sobre RN-2")
    public void sistema_rejeita_candidatura_rn2() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-2"));
    }
}
