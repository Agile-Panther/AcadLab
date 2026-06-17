package school.cesar.acadlab.dominio.estagios.estagio;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import school.cesar.acadlab.dominio.estagios.EstagiosFuncionalidade;
import school.cesar.acadlab.dominio.estagios.oportunidade.CoordenadorId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EmpresaId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EstudanteId;

public class EncerrarEstagioFuncionalidade extends EstagiosFuncionalidade {

    @io.cucumber.java.en.Given("um estágio em andamento para o estudante de id {int}")
    public void um_estagio_em_andamento(int estudanteId) {
        criarEstagioEmAndamento(estudanteId);
    }

    @When("o estudante solicita o encerramento do estágio")
    public void o_estudante_solicita_encerramento() {
        servico.solicitarEncerramento(estagioId);
    }

    @Then("o estágio possui status ENCERRAMENTO_SOLICITADO")
    public void o_estagio_possui_status_encerramento_solicitado() {
        var estagio = estagioRepositorio.buscarPorId(estagioId).orElseThrow();
        assertEquals(StatusEstagio.ENCERRAMENTO_SOLICITADO, estagio.getStatus());
    }

    @When("o coordenador de id {int} homologa o encerramento")
    public void o_coordenador_homologa(int coordenadorId) {
        servico.homologarEncerramento(estagioId, new CoordenadorId(coordenadorId));
    }

    @Then("o estágio possui status ENCERRADO")
    public void o_estagio_possui_status_encerrado() {
        var estagio = estagioRepositorio.buscarPorId(estagioId).orElseThrow();
        assertEquals(StatusEstagio.ENCERRADO, estagio.getStatus());
    }

    @Given("um estágio com encerramento já solicitado")
    public void um_estagio_com_encerramento_solicitado() {
        var oportunidadeId = servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        servico.candidatar(oportunidadeId, new EstudanteId(20));
        servico.encaminhar(oportunidadeId, new CoordenadorId(30));
        estagioId = servico.confirmar(oportunidadeId, new EmpresaId(10));
        servico.solicitarEncerramento(estagioId);
    }

    @When("o estudante tenta solicitar encerramento novamente")
    public void o_estudante_tenta_solicitar_encerramento_novamente() {
        try {
            servico.solicitarEncerramento(estagioId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o sistema rejeita o encerramento com mensagem sobre RN-11")
    public void sistema_rejeita_encerramento_rn11() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-11"));
    }

    @When("o coordenador de id {int} tenta homologar sem solicitação")
    public void o_coordenador_tenta_homologar_sem_solicitacao(int coordenadorId) {
        try {
            servico.homologarEncerramento(estagioId, new CoordenadorId(coordenadorId));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o sistema rejeita a homologação com mensagem sobre RN-12")
    public void sistema_rejeita_homologacao_rn12() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-12"));
    }
}
