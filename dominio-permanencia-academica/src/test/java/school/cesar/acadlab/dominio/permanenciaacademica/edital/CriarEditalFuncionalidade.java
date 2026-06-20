package school.cesar.acadlab.dominio.permanenciaacademica.edital;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.permanenciaacademica.PermanenciaAcademicaFuncionalidade;
import school.cesar.acadlab.dominio.permanenciaacademica.StatusEdital;

public class CriarEditalFuncionalidade {
    private final PermanenciaAcademicaFuncionalidade ctx;

    public CriarEditalFuncionalidade(PermanenciaAcademicaFuncionalidade ctx) {
        this.ctx = ctx;
    }

    private static java.time.LocalDate hoje() { return LocalDate.now(); }

    private void criarEditalParaPrograma(String programa) {
        ctx.editalServico.criar(programa, null, 5,
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
        ctx.editalServico.criar(programa, null, vagas,
                hoje(), hoje().plusDays(10),
                hoje().plusDays(11), hoje().plusDays(20),
                hoje().plusDays(180));
    }

    @Entao("o sistema registra o edital com status de inscrições abertas")
    public void o_sistema_registra_edital() {
        var editais = ctx.repositorio.buscarPorPrograma("Bolsa Permanência");
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
            ctx.editalServico.criar(programa, null, vagas,
                    hoje(), hoje().plusDays(10),
                    hoje().plusDays(11), hoje().plusDays(20),
                    hoje().plusDays(180));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o sistema informa que já existe um edital com inscrições abertas para o programa")
    public void o_sistema_informa_edital_existente() {
        assertNotNull(ctx.excecao);
        assertInstanceOf(IllegalStateException.class, ctx.excecao);
    }

    @Quando("a secretaria cria um edital sem prazo de renovação para o programa {string} com {int} vagas")
    public void secretaria_cria_edital_sem_renovacao(String programa, int vagas) {
        try {
            ctx.editalServico.criar(programa, null, vagas,
                    hoje(), hoje().plusDays(10),
                    hoje().plusDays(11), hoje().plusDays(20),
                    null);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o edital é registrado para o programa {string}")
    public void o_edital_e_registrado_para_o_programa(String programa) {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        var editais = ctx.repositorio.buscarPorPrograma(programa);
        assertFalse(editais.isEmpty());
        assertEquals(StatusEdital.INSCRICOES_ABERTAS, editais.get(0).getStatus());
        assertNull(editais.get(0).getPrazoRenovacao());
    }
}
