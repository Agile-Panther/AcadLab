package school.cesar.acadlab.dominio.permanenciaacademica.edital;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.permanenciaacademica.Edital;
import school.cesar.acadlab.dominio.permanenciaacademica.EditalId;
import school.cesar.acadlab.dominio.permanenciaacademica.PermanenciaAcademicaFuncionalidade;
import school.cesar.acadlab.dominio.permanenciaacademica.StatusEdital;

public class PublicarResultadoFuncionalidade {
    private final PermanenciaAcademicaFuncionalidade ctx;
    private EditalId editalId;

    public PublicarResultadoFuncionalidade(PermanenciaAcademicaFuncionalidade ctx) {
        this.ctx = ctx;
    }

    private static final String PROGRAMA = "Bolsa Permanência";

    private EditalId criarEditalComStatus(LocalDate prazoRecursoFim) {
        var id = ctx.repositorio.proximoEditalId();
        var edital = Edital.reconstituir(id, PROGRAMA, 5,
                LocalDate.now().minusDays(20), LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(9), prazoRecursoFim,
                LocalDate.now().plusDays(180), StatusEdital.INSCRICOES_ABERTAS);
        ctx.repositorio.salvar(edital);
        return id;
    }

    @Dado("existe um edital com inscrições encerradas e prazo de recurso expirado")
    public void edital_com_recurso_expirado() {
        editalId = criarEditalComStatus(LocalDate.now().minusDays(1));
    }

    @Quando("a assistência estudantil publica o resultado final")
    public void publica_resultado() {
        ctx.editalServico.publicarResultado(editalId, LocalDate.now());
    }

    @Entao("o sistema atualiza o status do edital para resultado publicado")
    public void status_resultado_publicado() {
        var edital = ctx.repositorio.obter(editalId);
        assertEquals(StatusEdital.RESULTADO_PUBLICADO, edital.getStatus());
    }

    @Dado("existe um edital com inscrições encerradas e prazo de recurso ainda aberto")
    public void edital_com_recurso_aberto() {
        editalId = criarEditalComStatus(LocalDate.now().plusDays(5));
    }

    @Quando("a assistência estudantil tenta publicar o resultado final")
    public void tenta_publicar_resultado() {
        try {
            ctx.editalServico.publicarResultado(editalId, LocalDate.now());
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o sistema informa que o prazo de recursos ainda não encerrou")
    public void informa_prazo_recurso_aberto() {
        assertNotNull(ctx.excecao);
        assertInstanceOf(IllegalStateException.class, ctx.excecao);
    }

    @Dado("existe um edital com resultado final publicado")
    public void edital_com_resultado_publicado() {
        editalId = criarEditalComStatus(LocalDate.now().minusDays(1));
        ctx.editalServico.publicarResultado(editalId, LocalDate.now());
    }

    @Quando("a secretaria encerra o edital")
    public void secretaria_encerra_edital() {
        ctx.editalServico.encerrar(editalId);
    }

    @Entao("o sistema atualiza o status do edital para encerrado")
    public void status_encerrado() {
        var edital = ctx.repositorio.obter(editalId);
        assertEquals(StatusEdital.ENCERRADO, edital.getStatus());
    }

    @Dado("existe um edital com inscrições abertas")
    public void edital_com_inscricoes_abertas() {
        editalId = criarEditalComStatus(LocalDate.now().plusDays(10));
    }

    @Quando("a secretaria tenta encerrar o edital")
    public void tenta_encerrar_edital() {
        try {
            ctx.editalServico.encerrar(editalId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o sistema informa que o resultado final ainda não foi publicado")
    public void informa_resultado_nao_publicado() {
        assertNotNull(ctx.excecao);
        assertInstanceOf(IllegalStateException.class, ctx.excecao);
    }
}
