package school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.secretariavirtual.SecretariaVirtualFuncionalidade;
import school.cesar.acadlab.dominio.secretariavirtual.analista.SecretariaId;
import school.cesar.acadlab.dominio.secretariavirtual.estudante.EstudanteId;
import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;

public class CancelarSolicitacaoFuncionalidade extends SecretariaVirtualFuncionalidade {
    private final EstudanteId estudanteId = new EstudanteId(40);
    private final PeriodoLetivoId periodoLetivoId = new PeriodoLetivoId(40);
    private final SecretariaId secretariaId = new SecretariaId(3);
    private SolicitacaoAcademicaId solicitacaoId;
    private RuntimeException excecao;

    private SolicitacaoAcademica criarSolicitacaoBase() {
        calendarioDentroDoPrazo = true;
        return solicitacaoServico.abrirSolicitacao(
                estudanteId, periodoLetivoId, TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO,
                "Solicitação para cancelamento", List.of());
    }

    @Dado("uma solicitação com status pendente de análise para cancelamento")
    public void uma_solicitacao_pendente() {
        var solicitacao = criarSolicitacaoBase();
        solicitacaoId = solicitacao.getId();
    }

    @Dado("uma solicitação com status em análise para cancelamento")
    public void uma_solicitacao_em_analise() {
        var solicitacao = criarSolicitacaoBase();
        solicitacaoId = solicitacao.getId();
        analiseServico.iniciarAnalise(solicitacaoId, secretariaId);
    }

    @Dado("uma solicitação com status deferida para cancelamento")
    public void uma_solicitacao_deferida() {
        var solicitacao = criarSolicitacaoBase();
        solicitacaoId = solicitacao.getId();
        analiseServico.iniciarAnalise(solicitacaoId, secretariaId);
        analiseServico.deferir(solicitacaoId, secretariaId, "Ok", false);
    }

    @Quando("o estudante cancela a solicitação")
    public void o_estudante_cancela() {
        try {
            solicitacaoServico.cancelarSolicitacao(solicitacaoId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Quando("o estudante tenta cancelar a solicitação")
    public void o_estudante_tenta_cancelar() {
        try {
            solicitacaoServico.cancelarSolicitacao(solicitacaoId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("a solicitação é cancelada com sucesso")
    public void a_solicitacao_e_cancelada() {
        assertNull(excecao, "Não deveria ter lançado exceção");
        var solicitacao = consultaServico.obterPorId(solicitacaoId);
        assertEquals(StatusSolicitacao.CANCELADA, solicitacao.getStatus());
    }

    @Entao("o sistema rejeita o cancelamento")
    public void o_sistema_rejeita_cancelamento() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
    }
}
