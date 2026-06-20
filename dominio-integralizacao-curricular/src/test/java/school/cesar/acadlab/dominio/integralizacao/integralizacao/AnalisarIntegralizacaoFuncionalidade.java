package school.cesar.acadlab.dominio.integralizacao.integralizacao;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.integralizacao.CoordenadorId;
import school.cesar.acadlab.dominio.integralizacao.EstudanteId;
import school.cesar.acadlab.dominio.integralizacao.IntegralizacaoFuncionalidade;
import school.cesar.acadlab.dominio.integralizacao.MatrizCurricularId;
import school.cesar.acadlab.dominio.integralizacao.checklist.ItemChecklist;
import school.cesar.acadlab.dominio.integralizacao.checklist.TipoItemChecklist;

public class AnalisarIntegralizacaoFuncionalidade {

    private final IntegralizacaoFuncionalidade ctx;
    private final EstudanteId estudanteId = new EstudanteId(1);
    private final MatrizCurricularId matrizId = new MatrizCurricularId(1);
    private final CoordenadorId coordenadorId = new CoordenadorId(1);
    private IntegralizacaoId integralizacaoId;

    public AnalisarIntegralizacaoFuncionalidade(IntegralizacaoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma solicitação de análise de integralização iniciada")
    public void solicitacao_iniciada() {
        var integralizacao = ctx.integralizacaoServico.iniciarAnalise(estudanteId, matrizId);
        integralizacaoId = integralizacao.getId();
    }

    @Quando("a secretaria gera o checklist com todos os requisitos cumpridos")
    public void gerar_checklist_completo() {
        try {
            var itens = List.of(
                    new ItemChecklist(TipoItemChecklist.DISCIPLINAS_OBRIGATORIAS, "100% cumpridas", true),
                    new ItemChecklist(TipoItemChecklist.CARGA_OPTATIVA, "Carga mínima cumprida", true),
                    new ItemChecklist(TipoItemChecklist.HORAS_COMPLEMENTARES, "Horas cumpridas", true)
            );
            ctx.integralizacaoServico.gerarChecklist(integralizacaoId, itens);
            ctx.integralizacaoServico.registrarResultado(integralizacaoId, StatusIntegralizacao.APTO);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("a secretaria gera o checklist com pendências e registra resultado inapto")
    public void gerar_checklist_com_pendencia() {
        try {
            var itens = List.of(
                    new ItemChecklist(TipoItemChecklist.DISCIPLINAS_OBRIGATORIAS, "Faltam 2 disciplinas", false),
                    new ItemChecklist(TipoItemChecklist.HORAS_COMPLEMENTARES, "Horas cumpridas", true)
            );
            ctx.integralizacaoServico.gerarChecklist(integralizacaoId, itens);
            ctx.integralizacaoServico.registrarResultado(integralizacaoId, StatusIntegralizacao.INAPTO);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o coordenador aprova a aptidão do estudante")
    public void coordenador_aprova_aptidao() {
        try {
            ctx.integralizacaoServico.aprovarAptidao(integralizacaoId, coordenadorId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a integralização é registrada com resultado apto")
    public void integralizacao_com_resultado_apto() {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        var integralizacao = ctx.consultaServico.buscar(integralizacaoId);
        assertEquals(StatusIntegralizacao.APTO, integralizacao.getStatus());
    }

    @Entao("a integralização é registrada com resultado inapto")
    public void integralizacao_com_resultado_inapto() {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        var integralizacao = ctx.consultaServico.buscar(integralizacaoId);
        assertEquals(StatusIntegralizacao.INAPTO, integralizacao.getStatus());
    }

    @Entao("a aptidão do estudante é formalmente aprovada")
    public void aptidao_aprovada() {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        var integralizacao = ctx.consultaServico.buscar(integralizacaoId);
        assertTrue(integralizacao.aptidaoAprovada());
    }
}
