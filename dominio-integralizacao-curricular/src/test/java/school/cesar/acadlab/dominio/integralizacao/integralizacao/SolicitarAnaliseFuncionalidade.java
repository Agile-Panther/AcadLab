package school.cesar.acadlab.dominio.integralizacao.integralizacao;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import school.cesar.acadlab.dominio.integralizacao.ConsultaPendenciasPorta;
import school.cesar.acadlab.dominio.integralizacao.ConsultaPeriodoLetivoPorta;
import school.cesar.acadlab.dominio.integralizacao.ConsultaRequisitosIntegralizacaoPorta;
import school.cesar.acadlab.dominio.integralizacao.EstudanteId;
import school.cesar.acadlab.dominio.integralizacao.IntegralizacaoFuncionalidade;
import school.cesar.acadlab.dominio.integralizacao.IntegralizacaoServicoProxy;
import school.cesar.acadlab.dominio.integralizacao.MatrizCurricularId;

public class SolicitarAnaliseFuncionalidade {

    private final IntegralizacaoFuncionalidade ctx;
    private final EstudanteId estudanteId = new EstudanteId(1);
    private final MatrizCurricularId matrizId = new MatrizCurricularId(1);
    private IntegralizacaoCurricular integralizacaoCriada;
    private IntegralizacaoServicoProxy proxy;

    private boolean periodoEncerrado = true;
    private boolean possuiPendencias = false;

    public SolicitarAnaliseFuncionalidade(IntegralizacaoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    private void criarProxy() {
        ConsultaPeriodoLetivoPorta periodoPorta = e -> periodoEncerrado;
        ConsultaPendenciasPorta pendenciasPorta = e -> possuiPendencias;
        ConsultaRequisitosIntegralizacaoPorta requisitosPorta = (e, m) -> true;
        proxy = new IntegralizacaoServicoProxy(
                ctx.integralizacaoServico, ctx.integralizacaoRepositorio,
                periodoPorta, pendenciasPorta, requisitosPorta);
    }

    @Dado("um estudante com último período letivo encerrado e sem pendências")
    public void estudante_periodo_encerrado_sem_pendencias() {
        periodoEncerrado = true;
        possuiPendencias = false;
        criarProxy();
    }

    @Dado("um estudante com período letivo ainda em andamento")
    public void estudante_periodo_em_andamento() {
        periodoEncerrado = false;
        possuiPendencias = false;
        criarProxy();
    }

    @Dado("um estudante com pendências acadêmicas registradas")
    public void estudante_com_pendencias() {
        periodoEncerrado = true;
        possuiPendencias = true;
        criarProxy();
    }

    @Quando("o estudante solicita análise de conclusão de curso")
    public void solicitar_analise() {
        try {
            integralizacaoCriada = proxy.iniciarAnalise(estudanteId, matrizId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o estudante tenta solicitar análise de conclusão de curso")
    public void tentar_solicitar_analise() {
        try {
            integralizacaoCriada = proxy.iniciarAnalise(estudanteId, matrizId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a análise de integralização é iniciada com sucesso")
    public void analise_iniciada_com_sucesso() {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        assertNotNull(integralizacaoCriada);
    }

    @E("o status da integralização é {string}")
    public void status_integralizacao(String statusEsperado) {
        assertEquals(StatusIntegralizacao.valueOf(statusEsperado), integralizacaoCriada.getStatus());
    }
}
