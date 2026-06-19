package school.cesar.acadlab.dominio.integralizacao.integralizacao;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.integralizacao.ConsultaPendenciasPorta;
import school.cesar.acadlab.dominio.integralizacao.ConsultaPeriodoLetivoPorta;
import school.cesar.acadlab.dominio.integralizacao.ConsultaRequisitosIntegralizacaoPorta;
import school.cesar.acadlab.dominio.integralizacao.CoordenadorId;
import school.cesar.acadlab.dominio.integralizacao.EstudanteId;
import school.cesar.acadlab.dominio.integralizacao.IntegralizacaoFuncionalidade;
import school.cesar.acadlab.dominio.integralizacao.IntegralizacaoServicoProxy;
import school.cesar.acadlab.dominio.integralizacao.MatrizCurricularId;
import school.cesar.acadlab.dominio.integralizacao.checklist.ItemChecklist;
import school.cesar.acadlab.dominio.integralizacao.checklist.TipoItemChecklist;

public class AprovarAptidaoFuncionalidade extends IntegralizacaoFuncionalidade {

    private final EstudanteId estudanteId = new EstudanteId(1);
    private final MatrizCurricularId matrizId = new MatrizCurricularId(1);
    private final CoordenadorId coordenadorId = new CoordenadorId(1);
    private IntegralizacaoId integralizacaoId;
    private RuntimeException excecao;
    private IntegralizacaoServicoProxy proxy;
    private boolean requisitoCumprido = true;

    private void criarProxy() {
        ConsultaPeriodoLetivoPorta periodoPorta = e -> true;
        ConsultaPendenciasPorta pendenciasPorta = e -> false;
        ConsultaRequisitosIntegralizacaoPorta requisitosPorta = (e, m) -> requisitoCumprido;
        proxy = new IntegralizacaoServicoProxy(
                integralizacaoServico, integralizacaoRepositorio,
                periodoPorta, pendenciasPorta, requisitosPorta);
    }

    @Dado("uma integralização com resultado apto e requisitos cumpridos")
    public void integralizacao_apto_requisitos_cumpridos() {
        requisitoCumprido = true;
        criarProxy();
        var integralizacao = integralizacaoServico.iniciarAnalise(estudanteId, matrizId);
        integralizacaoId = integralizacao.getId();
        integralizacaoServico.gerarChecklist(integralizacaoId, checklistCompleto());
        integralizacaoServico.registrarResultado(integralizacaoId, StatusIntegralizacao.APTO);
    }

    @Dado("uma integralização com resultado apto mas requisitos não cumpridos")
    public void integralizacao_apto_requisitos_nao_cumpridos() {
        requisitoCumprido = false;
        criarProxy();
        var integralizacao = integralizacaoServico.iniciarAnalise(estudanteId, matrizId);
        integralizacaoId = integralizacao.getId();
        integralizacaoServico.gerarChecklist(integralizacaoId, checklistCompleto());
        integralizacaoServico.registrarResultado(integralizacaoId, StatusIntegralizacao.APTO);
    }

    @Dado("uma integralização com resultado inapto")
    public void integralizacao_inapto() {
        requisitoCumprido = true;
        criarProxy();
        var integralizacao = integralizacaoServico.iniciarAnalise(estudanteId, matrizId);
        integralizacaoId = integralizacao.getId();
        integralizacaoServico.gerarChecklist(integralizacaoId, checklistComPendencia());
        integralizacaoServico.registrarResultado(integralizacaoId, StatusIntegralizacao.INAPTO);
    }

    @Quando("o coordenador aprova a aptidão para colação de grau")
    public void coordenador_aprova() {
        try {
            proxy.aprovarAptidao(integralizacaoId, coordenadorId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Quando("o coordenador tenta aprovar a aptidão para colação de grau")
    public void coordenador_tenta_aprovar() {
        try {
            proxy.aprovarAptidao(integralizacaoId, coordenadorId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("a aptidão é formalmente aprovada com registro do coordenador")
    public void aptidao_aprovada() {
        assertNull(excecao, "Não deveria ter lançado exceção");
        var integralizacao = consultaServico.buscar(integralizacaoId);
        assertTrue(integralizacao.aptidaoAprovada());
        assertEquals(coordenadorId, integralizacao.getAprovadorId());
    }

    @Entao("o sistema deve rejeitar informando \"requisitos curriculares não foram cumpridos\"")
    public void rejeita_requisitos() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN6"));
    }

    @Entao("o sistema deve rejeitar informando \"resultado da análise de integralização é inapto\"")
    public void rejeita_resultado_nao_apto() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
    }

    private List<ItemChecklist> checklistCompleto() {
        return List.of(
                new ItemChecklist(TipoItemChecklist.DISCIPLINAS_OBRIGATORIAS, "100% cumpridas", true),
                new ItemChecklist(TipoItemChecklist.CARGA_OPTATIVA, "Carga mínima cumprida", true),
                new ItemChecklist(TipoItemChecklist.HORAS_COMPLEMENTARES, "Horas cumpridas", true)
        );
    }

    private List<ItemChecklist> checklistComPendencia() {
        return List.of(
                new ItemChecklist(TipoItemChecklist.DISCIPLINAS_OBRIGATORIAS, "Faltam disciplinas", false),
                new ItemChecklist(TipoItemChecklist.HORAS_COMPLEMENTARES, "Horas cumpridas", true)
        );
    }
}
