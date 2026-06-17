package school.cesar.acadlab.dominio.estagios.oportunidade;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import school.cesar.acadlab.dominio.estagios.EstagiosFuncionalidade;
import school.cesar.acadlab.dominio.estagios.estagio.StatusEstagio;

public class ConfirmarCandidaturaFuncionalidade extends EstagiosFuncionalidade {

    private OportunidadeId oportunidadeId;

    @Given("uma oportunidade encaminhada com candidato de id {int}")
    public void uma_oportunidade_encaminhada_com_candidato(int estudanteId) {
        oportunidadeId = servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        servico.candidatar(oportunidadeId, new EstudanteId(estudanteId));
        servico.encaminhar(oportunidadeId, new CoordenadorId(30));
    }

    @When("a empresa de id {int} confirma a candidatura")
    public void a_empresa_confirma(int empresaId) {
        estagioId = servico.confirmar(oportunidadeId, new EmpresaId(empresaId));
    }

    @Then("o estágio é criado com status EM_ANDAMENTO")
    public void o_estagio_e_criado_em_andamento() {
        var estagio = estagioRepositorio.buscarPorId(estagioId).orElseThrow();
        assertEquals(StatusEstagio.EM_ANDAMENTO, estagio.getStatus());
    }

    @Then("o estágio possui o estudante de id {int}")
    public void o_estagio_possui_estudante(int estudanteId) {
        var estagio = estagioRepositorio.buscarPorId(estagioId).orElseThrow();
        assertEquals(new EstudanteId(estudanteId), estagio.getEstudanteId());
    }

    @When("a empresa de id {int} recusa a candidatura")
    public void a_empresa_recusa(int empresaId) {
        servico.recusar(oportunidadeId, new EmpresaId(empresaId));
    }

    @Then("a oportunidade fica com status RECUSADA")
    public void a_oportunidade_fica_recusada() {
        var oportunidade = oportunidadeRepositorio.buscarPorId(oportunidadeId).orElseThrow();
        assertEquals(StatusOportunidade.RECUSADA, oportunidade.getStatus());
    }

    @Given("uma oportunidade aberta com candidato de id {int}")
    public void uma_oportunidade_aberta_com_candidato_bdd(int estudanteId) {
        oportunidadeId = servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        servico.candidatar(oportunidadeId, new EstudanteId(estudanteId));
    }

    @When("a empresa tenta confirmar sem encaminhamento")
    public void a_empresa_tenta_confirmar_sem_encaminhamento() {
        try {
            servico.confirmar(oportunidadeId, new EmpresaId(10));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o sistema rejeita a confirmação com mensagem sobre RN-5")
    public void sistema_rejeita_confirmacao_rn5() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-5"));
    }
}
