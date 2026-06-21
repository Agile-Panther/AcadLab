package school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.secretariavirtual.SecretariaVirtualFuncionalidade;
import school.cesar.acadlab.dominio.secretariavirtual.analista.SecretariaId;
import school.cesar.acadlab.dominio.secretariavirtual.documento.Documento;
import school.cesar.acadlab.dominio.secretariavirtual.estudante.EstudanteId;
import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;

public class ComplementarSolicitacaoFuncionalidade {
    private final SecretariaVirtualFuncionalidade ctx;
    private final EstudanteId estudanteId = new EstudanteId(20);
    private final PeriodoLetivoId periodoLetivoId = new PeriodoLetivoId(20);
    private final SecretariaId secretariaId = new SecretariaId(2);
    private SolicitacaoAcademicaId solicitacaoId;

    public ComplementarSolicitacaoFuncionalidade(SecretariaVirtualFuncionalidade ctx) {
        this.ctx = ctx;
    }

    private SolicitacaoAcademica criarSolicitacaoBase() {
        ctx.calendarioDentroDoPrazo = true;
        return ctx.solicitacaoServico.abrirSolicitacao(
                estudanteId, periodoLetivoId, TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO,
                "Solicitação para complementação", List.of());
    }

    @Dado("uma solicitação com status {string}")
    public void uma_solicitacao_com_status(String status) {
        var solicitacao = criarSolicitacaoBase();
        solicitacaoId = solicitacao.getId();

        switch (StatusSolicitacao.valueOf(status)) {
            case PENDENTE_COMPLEMENTACAO:
                ctx.analiseServico.iniciarAnalise(solicitacaoId, secretariaId);
                ctx.analiseServico.solicitarComplementacao(solicitacaoId, secretariaId);
                break;
            case CONCLUIDA:
                ctx.analiseServico.iniciarAnalise(solicitacaoId, secretariaId);
                ctx.analiseServico.deferir(solicitacaoId, secretariaId, "Ok", false);
                ctx.analiseServico.concluir(solicitacaoId);
                break;
            case INDEFERIDA:
                ctx.analiseServico.iniciarAnalise(solicitacaoId, secretariaId);
                ctx.analiseServico.indeferir(solicitacaoId, secretariaId, "Insuficiente");
                break;
            default:
                break;
        }
    }

    @Quando("o estudante complementa a solicitação com um documento")
    public void o_estudante_complementa() {
        try {
            var documento = new Documento("complemento", "complemento.pdf");
            ctx.solicitacaoServico.complementarSolicitacao(solicitacaoId, documento);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o estudante tenta complementar a solicitação")
    public void o_estudante_tenta_complementar() {
        try {
            var documento = new Documento("complemento", "complemento.pdf");
            ctx.solicitacaoServico.complementarSolicitacao(solicitacaoId, documento);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a solicitação volta para status {string}")
    public void a_solicitacao_volta_para_status(String statusEsperado) {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        var solicitacao = ctx.consultaServico.obterPorId(solicitacaoId);
        assertEquals(StatusSolicitacao.valueOf(statusEsperado), solicitacao.getStatus());
    }
}
