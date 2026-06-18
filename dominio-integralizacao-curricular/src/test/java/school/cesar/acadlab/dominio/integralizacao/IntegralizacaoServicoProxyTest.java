package school.cesar.acadlab.dominio.integralizacao;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.integralizacao.checklist.ItemChecklist;
import school.cesar.acadlab.dominio.integralizacao.checklist.TipoItemChecklist;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.StatusIntegralizacao;

class IntegralizacaoServicoProxyTest {

    private IntegralizacaoRepositorioTest repositorio;
    private IntegralizacaoServico servicoReal;
    private EstudanteId estudanteId;
    private MatrizCurricularId matrizId;
    private CoordenadorId coordenadorId;

    @BeforeEach
    void setUp() {
        repositorio = new IntegralizacaoRepositorioTest();
        servicoReal = new IntegralizacaoServico(repositorio);
        estudanteId = new EstudanteId(1);
        matrizId = new MatrizCurricularId(1);
        coordenadorId = new CoordenadorId(1);
    }

    private IntegralizacaoServicoProxy criarProxy(boolean periodoEncerrado,
                                                    boolean possuiPendencias,
                                                    boolean cumpreRequisitos) {
        return new IntegralizacaoServicoProxy(
                servicoReal, repositorio,
                e -> periodoEncerrado,
                e -> possuiPendencias,
                (e, m) -> cumpreRequisitos);
    }

    @Test
    void iniciarAnalise_periodoEncerradoSemPendencias_deveCriarIntegralizacao() {
        var proxy = criarProxy(true, false, true);

        var integralizacao = proxy.iniciarAnalise(estudanteId, matrizId);

        assertNotNull(integralizacao);
        assertEquals(StatusIntegralizacao.EM_ANALISE, integralizacao.getStatus());
    }

    @Test
    void iniciarAnalise_periodoNaoEncerrado_deveLancarExcecao() {
        var proxy = criarProxy(false, false, true);

        var excecao = assertThrows(IllegalStateException.class,
                () -> proxy.iniciarAnalise(estudanteId, matrizId));
        assertTrue(excecao.getMessage().contains("RN1"));
    }

    @Test
    void iniciarAnalise_comPendencias_deveLancarExcecao() {
        var proxy = criarProxy(true, true, true);

        var excecao = assertThrows(IllegalStateException.class,
                () -> proxy.iniciarAnalise(estudanteId, matrizId));
        assertTrue(excecao.getMessage().contains("RN2"));
    }

    @Test
    void aprovarAptidao_comRequisitosNaoCumpridos_deveLancarExcecao() {
        var proxy = criarProxy(true, false, false);

        var integralizacao = servicoReal.iniciarAnalise(estudanteId, matrizId);
        servicoReal.gerarChecklist(integralizacao.getId(), checklistCompleto());
        servicoReal.registrarResultado(integralizacao.getId(), StatusIntegralizacao.APTO);

        var excecao = assertThrows(IllegalStateException.class,
                () -> proxy.aprovarAptidao(integralizacao.getId(), coordenadorId));
        assertTrue(excecao.getMessage().contains("RN6"));
    }

    @Test
    void aprovarAptidao_comRequisitoCumprido_deveAprovar() {
        var proxy = criarProxy(true, false, true);

        var integralizacao = servicoReal.iniciarAnalise(estudanteId, matrizId);
        servicoReal.gerarChecklist(integralizacao.getId(), checklistCompleto());
        servicoReal.registrarResultado(integralizacao.getId(), StatusIntegralizacao.APTO);

        proxy.aprovarAptidao(integralizacao.getId(), coordenadorId);

        var atualizada = repositorio.obter(integralizacao.getId());
        assertTrue(atualizada.aptidaoAprovada());
    }

    @Test
    void gerarChecklist_deveDelegarParaServicoReal() {
        var proxy = criarProxy(true, false, true);

        var integralizacao = servicoReal.iniciarAnalise(estudanteId, matrizId);
        proxy.gerarChecklist(integralizacao.getId(), checklistCompleto());

        var atualizada = repositorio.obter(integralizacao.getId());
        assertEquals(3, atualizada.getItensChecklist().size());
    }

    private List<ItemChecklist> checklistCompleto() {
        return List.of(
                new ItemChecklist(TipoItemChecklist.DISCIPLINAS_OBRIGATORIAS, "100% cumpridas", true),
                new ItemChecklist(TipoItemChecklist.CARGA_OPTATIVA, "Carga mínima cumprida", true),
                new ItemChecklist(TipoItemChecklist.HORAS_COMPLEMENTARES, "Horas cumpridas", true)
        );
    }
}
