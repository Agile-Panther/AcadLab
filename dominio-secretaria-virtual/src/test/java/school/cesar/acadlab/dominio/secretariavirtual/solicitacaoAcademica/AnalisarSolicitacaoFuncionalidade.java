package school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import school.cesar.acadlab.dominio.secretariavirtual.SecretariaVirtualFuncionalidade;
import school.cesar.acadlab.dominio.secretariavirtual.analista.SecretariaId;
import school.cesar.acadlab.dominio.secretariavirtual.estudante.EstudanteId;
import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;

public class AnalisarSolicitacaoFuncionalidade extends SecretariaVirtualFuncionalidade {
    private final EstudanteId estudanteId = new EstudanteId(10);
    private final PeriodoLetivoId periodoLetivoId = new PeriodoLetivoId(10);
    private final SecretariaId secretariaId = new SecretariaId(1);
    private SolicitacaoAcademicaId solicitacaoId;
    private RuntimeException excecao;

    @Dado("uma solicitação pendente de análise")
    public void uma_solicitacao_pendente() {
        calendarioDentroDoPrazo = true;
        var solicitacao = solicitacaoServico.abrirSolicitacao(
                estudanteId, periodoLetivoId, TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO,
                "Solicitação para análise", List.of());
        solicitacaoId = solicitacao.getId();
    }

    @Quando("a secretaria inicia a análise da solicitação")
    public void a_secretaria_inicia_analise() {
        analiseServico.iniciarAnalise(solicitacaoId, secretariaId);
    }

    @E("a secretaria defere a solicitação sem impacto acadêmico")
    public void a_secretaria_defere_sem_impacto() {
        analiseServico.deferir(solicitacaoId, secretariaId, "Documentação correta", false);
    }

    @E("a secretaria defere a solicitação com impacto acadêmico")
    public void a_secretaria_defere_com_impacto() {
        analiseServico.deferir(solicitacaoId, secretariaId, "Correção necessária", true);
    }

    @E("a secretaria conclui a solicitação")
    public void a_secretaria_conclui() {
        try {
            analiseServico.concluir(solicitacaoId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @E("a secretaria tenta concluir a solicitação")
    public void a_secretaria_tenta_concluir() {
        try {
            analiseServico.concluir(solicitacaoId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @E("as alterações são vinculadas ao protocolo")
    public void as_alteracoes_sao_vinculadas() {
        analiseServico.vincularAlteracoesEConcluir(solicitacaoId);
    }

    @E("a secretaria indefere a solicitação")
    public void a_secretaria_indefere() {
        analiseServico.indeferir(solicitacaoId, secretariaId, "Documentação insuficiente");
    }

    @Entao("a solicitação é concluída com sucesso")
    public void a_solicitacao_e_concluida() {
        var solicitacao = consultaServico.obterPorId(solicitacaoId);
        assertEquals(StatusSolicitacao.CONCLUIDA, solicitacao.getStatus());
    }

    @Entao("o sistema rejeita a conclusão por falta de vinculação de alterações")
    public void o_sistema_rejeita_conclusao() {
        assertNotNull(excecao);
        assertTrue(excecao.getMessage().contains("vinculação"));
    }

    @Entao("a solicitação é indeferida com justificativa registrada")
    public void a_solicitacao_e_indeferida() {
        var solicitacao = consultaServico.obterPorId(solicitacaoId);
        assertEquals(StatusSolicitacao.INDEFERIDA, solicitacao.getStatus());
        assertNotNull(solicitacao.getJustificativaAnalise());
    }
}
