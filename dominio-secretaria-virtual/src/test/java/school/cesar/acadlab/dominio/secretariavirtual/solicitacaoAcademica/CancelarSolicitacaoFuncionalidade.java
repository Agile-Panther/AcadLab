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

public class CancelarSolicitacaoFuncionalidade {
    private final SecretariaVirtualFuncionalidade ctx;
    private final EstudanteId estudanteId = new EstudanteId(40);
    private final PeriodoLetivoId periodoLetivoId = new PeriodoLetivoId(40);
    private final SecretariaId secretariaId = new SecretariaId(3);
    private SolicitacaoAcademicaId solicitacaoId;

    public CancelarSolicitacaoFuncionalidade(SecretariaVirtualFuncionalidade ctx) {
        this.ctx = ctx;
    }

    private SolicitacaoAcademica criarSolicitacaoBase() {
        ctx.calendarioDentroDoPrazo = true;
        return ctx.solicitacaoServico.abrirSolicitacao(
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
        ctx.analiseServico.iniciarAnalise(solicitacaoId, secretariaId);
    }

    @Dado("uma solicitação com status deferida para cancelamento")
    public void uma_solicitacao_deferida() {
        var solicitacao = criarSolicitacaoBase();
        solicitacaoId = solicitacao.getId();
        ctx.analiseServico.iniciarAnalise(solicitacaoId, secretariaId);
        ctx.analiseServico.deferir(solicitacaoId, secretariaId, "Ok", false);
    }

    @Quando("o estudante cancela a solicitação")
    public void o_estudante_cancela() {
        try {
            ctx.solicitacaoServico.cancelarSolicitacao(solicitacaoId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o estudante tenta cancelar a solicitação")
    public void o_estudante_tenta_cancelar() {
        try {
            ctx.solicitacaoServico.cancelarSolicitacao(solicitacaoId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a solicitação é cancelada com sucesso")
    public void a_solicitacao_e_cancelada() {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        var solicitacao = ctx.consultaServico.obterPorId(solicitacaoId);
        assertEquals(StatusSolicitacao.CANCELADA, solicitacao.getStatus());
    }
}
