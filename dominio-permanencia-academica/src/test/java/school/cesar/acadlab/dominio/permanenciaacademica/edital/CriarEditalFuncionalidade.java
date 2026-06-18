package school.cesar.acadlab.dominio.permanenciaacademica.edital;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.permanenciaacademica.PermanenciaAcademicaFuncionalidade;
import school.cesar.acadlab.dominio.permanenciaacademica.StatusEdital;

public class CriarEditalFuncionalidade extends PermanenciaAcademicaFuncionalidade {
    private RuntimeException excecao;

    private static java.time.LocalDate hoje() { return LocalDate.now(); }

    private void criarEditalParaPrograma(String programa) {
        editalServico.criar(programa, 5,
                hoje(), hoje().plusDays(10),
                hoje().plusDays(11), hoje().plusDays(20),
                hoje().plusDays(180));
    }

    @Dado("não existe edital com inscrições abertas para o programa {string}")
    public void nao_existe_edital_aberto(String programa) {
        // estado inicial — repositório vazio
    }

    @Quando("a secretaria cria um edital para o programa {string} com {int} vagas")
    public void secretaria_cria_edital(String programa, int vagas) {
        editalServico.criar(programa, vagas,
                hoje(), hoje().plusDays(10),
                hoje().plusDays(11), hoje().plusDays(20),
                hoje().plusDays(180));
    }

    @Entao("o sistema registra o edital com status de inscrições abertas")
    public void o_sistema_registra_edital() {
        var editais = repositorio.buscarPorPrograma("Bolsa Permanência");
        assertFalse(editais.isEmpty());
        assertEquals(StatusEdital.INSCRICOES_ABERTAS, editais.get(0).getStatus());
    }

    @Dado("existe um edital com inscrições abertas para o programa {string}")
    public void existe_edital_aberto(String programa) {
        criarEditalParaPrograma(programa);
    }

    @Quando("a secretaria tenta criar um novo edital para o programa {string} com {int} vagas")
    public void secretaria_tenta_criar_edital(String programa, int vagas) {
        try {
            editalServico.criar(programa, vagas,
                    hoje(), hoje().plusDays(10),
                    hoje().plusDays(11), hoje().plusDays(20),
                    hoje().plusDays(180));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema informa que já existe um edital com inscrições abertas para o programa")
    public void o_sistema_informa_edital_existente() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
    }
}
