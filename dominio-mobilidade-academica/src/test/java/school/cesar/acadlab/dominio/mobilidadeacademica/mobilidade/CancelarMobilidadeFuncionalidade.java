package school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import school.cesar.acadlab.dominio.mobilidadeacademica.MobilidadeFuncionalidade;

public class CancelarMobilidadeFuncionalidade extends MobilidadeFuncionalidade {

    private MobilidadeAcademica mobilidade;
    private RuntimeException excecao;

    @Given("uma mobilidade solicitada para estudante id {int} sem período iniciado")
    public void uma_mobilidade_solicitada_sem_periodo_iniciado(int estudanteId) {
        var mobilidadeId = repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), "Tokyo University");
        repositorio.salvar(mobilidade);
    }

    @When("o estudante solicita cancelamento com justificativa {string} em {string}")
    public void o_estudante_solicita_cancelamento_com_justificativa_em(String justificativa, String dataStr) {
        LocalDate hoje = LocalDate.parse(dataStr);
        try {
            mobilidade.solicitarCancelamento(justificativa, hoje);
            repositorio.salvar(mobilidade);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @When("o coordenador confirma o cancelamento da mobilidade")
    public void o_coordenador_confirma_o_cancelamento_da_mobilidade() {
        mobilidade.confirmarCancelamento(new CoordenadorId(1));
        repositorio.salvar(mobilidade);
    }

    @Then("a mobilidade tem status CANCELADA")
    public void a_mobilidade_tem_status_cancelada() {
        assertEquals(StatusMobilidade.CANCELADA, mobilidade.getStatus());
    }

    @Given("uma mobilidade em andamento para estudante id {int} iniciada em {string}")
    public void uma_mobilidade_em_andamento_iniciada_em(int estudanteId, String dataStr) {
        var mobilidadeId = repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), "Seoul National University");
        mobilidade.autorizar(new CoordenadorId(1));
        mobilidade.iniciarPeriodoExterno(LocalDate.parse(dataStr));
        repositorio.salvar(mobilidade);
    }

    @When("o estudante tenta cancelar a mobilidade em andamento em {string}")
    public void o_estudante_tenta_cancelar_mobilidade_em_andamento_em(String dataStr) {
        LocalDate hoje = LocalDate.parse(dataStr);
        try {
            mobilidade.solicitarCancelamento("Desistência", hoje);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o sistema rejeita o cancelamento com mensagem sobre RN-7")
    public void o_sistema_rejeita_cancelamento_rn7() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-7"));
    }

    @Given("uma mobilidade solicitada para estudante id {int} sem justificativa de cancelamento")
    public void uma_mobilidade_solicitada_sem_justificativa_cancelamento(int estudanteId) {
        var mobilidadeId = repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), "UCL");
        repositorio.salvar(mobilidade);
    }

    @When("o coordenador tenta confirmar cancelamento sem justificativa prévia")
    public void o_coordenador_tenta_confirmar_cancelamento_sem_justificativa() {
        try {
            mobilidade.confirmarCancelamento(new CoordenadorId(1));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o sistema rejeita a confirmação com mensagem sobre RN-8")
    public void o_sistema_rejeita_confirmacao_rn8() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-8"));
    }
}
