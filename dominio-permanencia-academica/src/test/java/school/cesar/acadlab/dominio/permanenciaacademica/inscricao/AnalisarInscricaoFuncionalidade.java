package school.cesar.acadlab.dominio.permanenciaacademica.inscricao;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.permanenciaacademica.AssistenciaEstudantilId;
import school.cesar.acadlab.dominio.permanenciaacademica.Edital;
import school.cesar.acadlab.dominio.permanenciaacademica.EditalId;
import school.cesar.acadlab.dominio.permanenciaacademica.EstudantePermanenciaId;
import school.cesar.acadlab.dominio.permanenciaacademica.Inscricao;
import school.cesar.acadlab.dominio.permanenciaacademica.InscricaoId;
import school.cesar.acadlab.dominio.permanenciaacademica.PermanenciaAcademicaFuncionalidade;
import school.cesar.acadlab.dominio.permanenciaacademica.StatusEdital;
import school.cesar.acadlab.dominio.permanenciaacademica.StatusInscricao;

public class AnalisarInscricaoFuncionalidade extends PermanenciaAcademicaFuncionalidade {
    private final EstudantePermanenciaId estudanteId = new EstudantePermanenciaId(1);
    private final AssistenciaEstudantilId assistenciaId = new AssistenciaEstudantilId(1);
    private EditalId editalId;
    private InscricaoId inscricaoId;
    private RuntimeException excecao;

    private EditalId criarEdital(LocalDate prazoRecursoInicio, LocalDate prazoRecursoFim) {
        var id = repositorio.proximoEditalId();
        var edital = Edital.reconstituir(id, "Bolsa Permanência", 5,
                LocalDate.now().minusDays(10), LocalDate.now().minusDays(1),
                prazoRecursoInicio, prazoRecursoFim,
                LocalDate.now().plusDays(180), StatusEdital.INSCRICOES_ABERTAS);
        repositorio.salvar(edital);
        return id;
    }

    private InscricaoId criarInscricaoPendente(EditalId eid) {
        var id = repositorio.proximoInscricaoId();
        var inscricao = new Inscricao(id, eid, estudanteId);
        repositorio.salvar(inscricao);
        return id;
    }

    @Dado("existe uma inscrição pendente no edital")
    public void inscricao_pendente() {
        editalId = criarEdital(LocalDate.now(), LocalDate.now().plusDays(5));
        inscricaoId = criarInscricaoPendente(editalId);
    }

    @Quando("a assistência estudantil defere a inscrição com pontuação {int}")
    public void defere_inscricao(int pontuacao) {
        inscricaoServico.deferir(inscricaoId, assistenciaId, pontuacao);
    }

    @Entao("o sistema atualiza o status da inscrição para deferida")
    public void status_deferida() {
        var inscricao = repositorio.obter(inscricaoId);
        assertEquals(StatusInscricao.DEFERIDA, inscricao.getStatus());
    }

    @Quando("a assistência estudantil indefere a inscrição")
    public void indefere_inscricao() {
        inscricaoServico.indeferir(inscricaoId, assistenciaId);
    }

    @Entao("o sistema atualiza o status da inscrição para indeferida")
    public void status_indeferida() {
        var inscricao = repositorio.obter(inscricaoId);
        assertEquals(StatusInscricao.INDEFERIDA, inscricao.getStatus());
    }

    @Dado("existe uma inscrição indeferida e o prazo de recurso está aberto")
    public void inscricao_indeferida_recurso_aberto() {
        editalId = criarEdital(LocalDate.now(), LocalDate.now().plusDays(5));
        inscricaoId = criarInscricaoPendente(editalId);
        inscricaoServico.indeferir(inscricaoId, assistenciaId);
    }

    @Quando("o estudante interpõe recurso contra o indeferimento")
    public void interpoe_recurso() {
        inscricaoServico.interporRecurso(inscricaoId, editalId, LocalDate.now());
    }

    @Entao("o sistema registra o recurso e atualiza o status para recurso interposto")
    public void status_recurso_interposto() {
        var inscricao = repositorio.obter(inscricaoId);
        assertEquals(StatusInscricao.RECURSO_INTERPOSTO, inscricao.getStatus());
        assertTrue(inscricao.isRecursoInterposto());
    }

    @Dado("existe uma inscrição com recurso já interposto")
    public void inscricao_com_recurso_ja_interposto() {
        editalId = criarEdital(LocalDate.now(), LocalDate.now().plusDays(5));
        inscricaoId = criarInscricaoPendente(editalId);
        inscricaoServico.indeferir(inscricaoId, assistenciaId);
        inscricaoServico.interporRecurso(inscricaoId, editalId, LocalDate.now());
    }

    @Quando("o estudante tenta interpor novo recurso")
    public void tenta_interpor_recurso() {
        try {
            inscricaoServico.interporRecurso(inscricaoId, editalId, LocalDate.now());
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema informa que já foi interposto um recurso para esta inscrição")
    public void sistema_informa_recurso_ja_interposto() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
    }
}
