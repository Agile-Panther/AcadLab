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

public class PublicarResultadoFuncionalidade extends PermanenciaAcademicaFuncionalidade {
    private EditalId editalId;
    private RuntimeException excecao;

    private static final String PROGRAMA = "Bolsa Permanência";

    private EditalId criarEditalComStatus(LocalDate prazoRecursoFim) {
        var id = repositorio.proximoEditalId();
        var edital = Edital.reconstituir(id, PROGRAMA, 5,
                LocalDate.now().minusDays(20), LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(9), prazoRecursoFim,
                LocalDate.now().plusDays(180), StatusEdital.INSCRICOES_ABERTAS);
        repositorio.salvar(edital);
        return id;
    }

    @Dado("existe um edital com inscrições encerradas e prazo de recurso expirado")
    public void edital_com_recurso_expirado() {
        editalId = criarEditalComStatus(LocalDate.now().minusDays(1));
    }

    @Quando("a assistência estudantil publica o resultado final")
    public void publica_resultado() {
        editalServico.publicarResultado(editalId, LocalDate.now());
    }

    @Entao("o sistema atualiza o status do edital para resultado publicado")
    public void status_resultado_publicado() {
        var edital = repositorio.obter(editalId);
        assertEquals(StatusEdital.RESULTADO_PUBLICADO, edital.getStatus());
    }

    @Dado("existe um edital com inscrições encerradas e prazo de recurso ainda aberto")
    public void edital_com_recurso_aberto() {
        editalId = criarEditalComStatus(LocalDate.now().plusDays(5));
    }

    @Quando("a assistência estudantil tenta publicar o resultado final")
    public void tenta_publicar_resultado() {
        try {
            editalServico.publicarResultado(editalId, LocalDate.now());
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema informa que o prazo de recursos ainda não encerrou")
    public void informa_prazo_recurso_aberto() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
    }

    @Dado("existe um edital com resultado final publicado")
    public void edital_com_resultado_publicado() {
        editalId = criarEditalComStatus(LocalDate.now().minusDays(1));
        editalServico.publicarResultado(editalId, LocalDate.now());
    }

    @Quando("a secretaria encerra o edital")
    public void secretaria_encerra_edital() {
        editalServico.encerrar(editalId);
    }

    @Entao("o sistema atualiza o status do edital para encerrado")
    public void status_encerrado() {
        var edital = repositorio.obter(editalId);
        assertEquals(StatusEdital.ENCERRADO, edital.getStatus());
    }

    @Dado("existe um edital com inscrições abertas")
    public void edital_com_inscricoes_abertas() {
        editalId = criarEditalComStatus(LocalDate.now().plusDays(10));
    }

    @Quando("a secretaria tenta encerrar o edital")
    public void tenta_encerrar_edital() {
        try {
            editalServico.encerrar(editalId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema informa que o resultado final ainda não foi publicado")
    public void informa_resultado_nao_publicado() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
    }
}
