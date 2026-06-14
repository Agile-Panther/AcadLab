package school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import school.cesar.acadlab.dominio.mobilidadeacademica.MobilidadeFuncionalidade;

public class SolicitarMobilidadeFuncionalidade extends MobilidadeFuncionalidade {

    private MobilidadeAcademica mobilidade;
    private RuntimeException excecao;

    @Given("um estudante com id {int} deseja mobilidade para {string}")
    public void um_estudante_com_id_deseja_mobilidade_para(int estudanteId, String instituicao) {
        var mobilidadeId = repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), instituicao);
        repositorio.salvar(mobilidade);
    }

    @When("o estudante solicita a mobilidade acadêmica")
    public void o_estudante_solicita_a_mobilidade_academica() {
        // mobilidade já foi criada no Given
    }

    @Then("a mobilidade é registrada com status SOLICITADA")
    public void a_mobilidade_e_registrada_com_status_solicitada() {
        assertEquals(StatusMobilidade.SOLICITADA, mobilidade.getStatus());
    }

    @When("o coordenador com id {int} autoriza a mobilidade")
    public void o_coordenador_com_id_autoriza_a_mobilidade(int coordenadorId) {
        mobilidade.autorizar(new CoordenadorId(coordenadorId));
        repositorio.salvar(mobilidade);
    }

    @Then("a mobilidade tem status AUTORIZADA")
    public void a_mobilidade_tem_status_autorizada() {
        assertEquals(StatusMobilidade.AUTORIZADA, mobilidade.getStatus());
    }

    @Given("uma mobilidade académica já autorizada para o estudante com id {int}")
    public void uma_mobilidade_ja_autorizada_para_o_estudante_com_id(int estudanteId) {
        var mobilidadeId = repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), "Stanford");
        mobilidade.autorizar(new CoordenadorId(1));
        repositorio.salvar(mobilidade);
    }

    @When("o coordenador com id {int} tenta autorizar a mobilidade já autorizada")
    public void o_coordenador_tenta_autorizar_a_mobilidade_ja_autorizada(int coordenadorId) {
        try {
            mobilidade.autorizar(new CoordenadorId(coordenadorId));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o sistema rejeita a autorização com mensagem sobre RN-1")
    public void o_sistema_rejeita_autorizacao_rn1() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-1"));
    }
}
