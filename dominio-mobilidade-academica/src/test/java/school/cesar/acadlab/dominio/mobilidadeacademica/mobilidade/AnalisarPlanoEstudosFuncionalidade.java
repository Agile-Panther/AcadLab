package school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import school.cesar.acadlab.dominio.mobilidadeacademica.MobilidadeFuncionalidade;

public class AnalisarPlanoEstudosFuncionalidade extends MobilidadeFuncionalidade {

    private MobilidadeAcademica mobilidade;
    private RuntimeException excecao;

    @Given("uma mobilidade autorizada para análise de plano com estudante id {int}")
    public void uma_mobilidade_autorizada_para_analise_de_plano_com_estudante_id(int estudanteId) {
        var mobilidadeId = repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), "Sorbonne");
        mobilidade.autorizar(new CoordenadorId(1));
        repositorio.salvar(mobilidade);
    }

    @When("o coordenador adiciona item ao plano com disciplina externa {int} equivalente {int} carga externa {int} carga equivalente {int}")
    public void o_coordenador_adiciona_item_ao_plano(int discExterna, int discEquivalente,
                                                      int cargaExterna, int cargaEquivalente) {
        mobilidade.adicionarItemPlano(
                new DisciplinaId(discExterna),
                new DisciplinaId(discEquivalente),
                cargaExterna,
                cargaEquivalente);
        repositorio.salvar(mobilidade);
    }

    @Then("o item do plano tem status AUTORIZADO")
    public void o_item_do_plano_tem_status_autorizado() {
        assertFalse(mobilidade.getPlanoEstudos().isEmpty());
        assertEquals(StatusItemPlano.AUTORIZADO, mobilidade.getPlanoEstudos().get(0).getStatus());
    }

    @When("o coordenador tenta adicionar item ao plano com carga externa {int} menor que equivalente {int}")
    public void o_coordenador_tenta_adicionar_item_carga_insuficiente(int cargaExterna, int cargaEquivalente) {
        try {
            mobilidade.adicionarItemPlano(
                    new DisciplinaId(10),
                    new DisciplinaId(20),
                    cargaExterna,
                    cargaEquivalente);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o sistema rejeita o item com mensagem sobre RN-3")
    public void o_sistema_rejeita_item_rn3() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-3"));
    }

    @Given("uma mobilidade autorizada sem itens no plano para estudante id {int}")
    public void uma_mobilidade_autorizada_sem_itens_no_plano_para_estudante_id(int estudanteId) {
        var mobilidadeId = repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), "Oxford");
        mobilidade.autorizar(new CoordenadorId(1));
        repositorio.salvar(mobilidade);
    }

    @When("a secretaria tenta registrar resultado para disciplina {int} fora do plano")
    public void a_secretaria_tenta_registrar_resultado_para_disciplina_fora_do_plano(int discId) {
        try {
            mobilidade.registrarResultado(new DisciplinaId(discId), new SecretariaId(1));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o sistema rejeita o registro com mensagem sobre ausência no plano")
    public void o_sistema_rejeita_registro_ausencia_no_plano() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
    }
}
