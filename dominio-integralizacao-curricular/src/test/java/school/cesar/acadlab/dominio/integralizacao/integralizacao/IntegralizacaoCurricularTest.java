package school.cesar.acadlab.dominio.integralizacao.integralizacao;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.integralizacao.CoordenadorId;
import school.cesar.acadlab.dominio.integralizacao.EstudanteId;
import school.cesar.acadlab.dominio.integralizacao.MatrizCurricularId;
import school.cesar.acadlab.dominio.integralizacao.checklist.ItemChecklist;
import school.cesar.acadlab.dominio.integralizacao.checklist.TipoItemChecklist;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoCurricular.AptidaoAprovadaEvento;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoCurricular.ResultadoRegistradoEvento;

class IntegralizacaoCurricularTest {

    private final IntegralizacaoId id = new IntegralizacaoId(1);
    private final EstudanteId estudanteId = new EstudanteId(1);
    private final MatrizCurricularId matrizId = new MatrizCurricularId(1);
    private final CoordenadorId coordenadorId = new CoordenadorId(1);

    private IntegralizacaoCurricular criarIntegralizacao() {
        return new IntegralizacaoCurricular(id, estudanteId, matrizId);
    }

    private List<ItemChecklist> checklistCompleto() {
        return List.of(
                new ItemChecklist(TipoItemChecklist.DISCIPLINAS_OBRIGATORIAS, "100% das obrigatórias", true),
                new ItemChecklist(TipoItemChecklist.CARGA_OPTATIVA, "Carga optativa mínima", true),
                new ItemChecklist(TipoItemChecklist.HORAS_COMPLEMENTARES, "Horas complementares", true)
        );
    }

    private List<ItemChecklist> checklistComPendencia() {
        return List.of(
                new ItemChecklist(TipoItemChecklist.DISCIPLINAS_OBRIGATORIAS, "100% das obrigatórias", false),
                new ItemChecklist(TipoItemChecklist.CARGA_OPTATIVA, "Carga optativa mínima", true)
        );
    }

    @Test
    void novaIntegralizacao_deveIniciarComStatusEmAnalise() {
        var integralizacao = criarIntegralizacao();
        assertEquals(StatusIntegralizacao.EM_ANALISE, integralizacao.getStatus());
    }

    @Test
    void gerarChecklist_deveRegistrarItensNoAgregado() {
        var integralizacao = criarIntegralizacao();

        var evento = integralizacao.gerarChecklist(checklistCompleto());

        assertNotNull(evento);
        assertEquals(3, integralizacao.getItensChecklist().size());
    }

    @Test
    void registrarResultado_comApto_deveAlterarStatus() {
        var integralizacao = criarIntegralizacao();
        integralizacao.gerarChecklist(checklistCompleto());

        var evento = integralizacao.registrarResultado(StatusIntegralizacao.APTO);

        assertNotNull(evento);
        assertInstanceOf(ResultadoRegistradoEvento.class, evento);
        assertEquals(StatusIntegralizacao.APTO, integralizacao.getStatus());
    }

    @Test
    void registrarResultado_comInaptoSemPendencia_deveLancarExcecao() {
        var integralizacao = criarIntegralizacao();
        integralizacao.gerarChecklist(checklistCompleto());

        assertThrows(IllegalStateException.class,
                () -> integralizacao.registrarResultado(StatusIntegralizacao.INAPTO));
    }

    @Test
    void registrarResultado_comInaptoEPendencia_deveRegistrarComSucesso() {
        var integralizacao = criarIntegralizacao();
        integralizacao.gerarChecklist(checklistComPendencia());

        var evento = integralizacao.registrarResultado(StatusIntegralizacao.INAPTO);

        assertNotNull(evento);
        assertEquals(StatusIntegralizacao.INAPTO, integralizacao.getStatus());
    }

    @Test
    void aprovarAptidao_comStatusApto_deveRegistrarAprovador() {
        var integralizacao = criarIntegralizacao();
        integralizacao.gerarChecklist(checklistCompleto());
        integralizacao.registrarResultado(StatusIntegralizacao.APTO);

        var evento = integralizacao.aprovarAptidao(coordenadorId);

        assertNotNull(evento);
        assertInstanceOf(AptidaoAprovadaEvento.class, evento);
        assertTrue(integralizacao.aptidaoAprovada());
        assertEquals(coordenadorId, integralizacao.getAprovadorId());
    }

    @Test
    void aprovarAptidao_comStatusInapto_deveLancarExcecao() {
        var integralizacao = criarIntegralizacao();
        integralizacao.gerarChecklist(checklistComPendencia());
        integralizacao.registrarResultado(StatusIntegralizacao.INAPTO);

        assertThrows(IllegalStateException.class,
                () -> integralizacao.aprovarAptidao(coordenadorId));
    }
}
