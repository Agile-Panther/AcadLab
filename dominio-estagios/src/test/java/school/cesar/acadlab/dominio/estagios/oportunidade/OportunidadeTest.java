package school.cesar.acadlab.dominio.estagios.oportunidade;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OportunidadeTest {

    private OportunidadeId id;
    private EmpresaId empresaId;
    private EstudanteId estudanteId;
    private CoordenadorId coordenadorId;

    @BeforeEach
    void setUp() {
        id = new OportunidadeId(1);
        empresaId = new EmpresaId(10);
        estudanteId = new EstudanteId(20);
        coordenadorId = new CoordenadorId(30);
    }

    @Test
    void deveCriarOportunidadeAberta() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        assertEquals(StatusOportunidade.ABERTA, oportunidade.getStatus());
        assertNull(oportunidade.getCandidato());
    }

    @Test
    void deveCandidatarEstudante() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        oportunidade.candidatar(estudanteId);
        assertEquals(estudanteId, oportunidade.getCandidato());
    }

    @Test
    void deveRejeitarCandidaturaEmOportunidadeNaoAberta() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        oportunidade.candidatar(estudanteId);
        oportunidade.encaminhar(coordenadorId);
        var ex = assertThrows(IllegalStateException.class,
                () -> oportunidade.candidatar(new EstudanteId(99)));
        assertTrue(ex.getMessage().contains("RN-1"));
    }

    @Test
    void deveRejeitarSegundaCandidatura() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        oportunidade.candidatar(estudanteId);
        var ex = assertThrows(IllegalStateException.class,
                () -> oportunidade.candidatar(new EstudanteId(99)));
        assertTrue(ex.getMessage().contains("RN-2"));
    }

    @Test
    void deveEncaminharComCandidato() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        oportunidade.candidatar(estudanteId);
        oportunidade.encaminhar(coordenadorId);
        assertEquals(StatusOportunidade.ENCAMINHADA, oportunidade.getStatus());
    }

    @Test
    void deveRejeitarEncaminhamentoSemCandidato() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        var ex = assertThrows(IllegalStateException.class,
                () -> oportunidade.encaminhar(coordenadorId));
        assertTrue(ex.getMessage().contains("RN-4"));
    }

    @Test
    void deveConfirmarCandidatura() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        oportunidade.candidatar(estudanteId);
        oportunidade.encaminhar(coordenadorId);
        oportunidade.confirmar(empresaId);
        assertEquals(StatusOportunidade.CONFIRMADA, oportunidade.getStatus());
    }

    @Test
    void deveRecusarCandidatura() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        oportunidade.candidatar(estudanteId);
        oportunidade.encaminhar(coordenadorId);
        oportunidade.recusar(empresaId);
        assertEquals(StatusOportunidade.RECUSADA, oportunidade.getStatus());
    }

    @Test
    void deveRejeitarConfirmarSemEncaminhar() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        oportunidade.candidatar(estudanteId);
        var ex = assertThrows(IllegalStateException.class,
                () -> oportunidade.confirmar(empresaId));
        assertTrue(ex.getMessage().contains("RN-5"));
    }
}
