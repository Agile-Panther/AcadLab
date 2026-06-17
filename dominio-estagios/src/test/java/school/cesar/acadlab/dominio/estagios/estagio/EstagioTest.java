package school.cesar.acadlab.dominio.estagios.estagio;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.estagios.oportunidade.CoordenadorId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EmpresaId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EstudanteId;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeId;

class EstagioTest {

    private Estagio estagio;
    private CoordenadorId coordenadorId;

    @BeforeEach
    void setUp() {
        estagio = new Estagio(new EstagioId(1), new OportunidadeId(1),
                new EstudanteId(1), new EmpresaId(1));
        coordenadorId = new CoordenadorId(10);
    }

    @Test
    void deveCriarEstagioEmAndamento() {
        assertEquals(StatusEstagio.EM_ANDAMENTO, estagio.getStatus());
        assertTrue(estagio.getRelatorios().isEmpty());
    }

    @Test
    void deveSubmeterRelatorio() {
        estagio.submeterRelatorio(1, "Relatório mensal");
        assertEquals(1, estagio.getRelatorios().size());
        assertEquals(StatusRelatorio.PENDENTE, estagio.getRelatorios().get(0).getStatus());
    }

    @Test
    void deveRejeitarRelatorioNumeroDuplicado() {
        estagio.submeterRelatorio(1, "Primeiro relatório");
        var ex = assertThrows(IllegalStateException.class,
                () -> estagio.submeterRelatorio(1, "Duplicado"));
        assertTrue(ex.getMessage().contains("RN-8"));
    }

    @Test
    void deveAprovarRelatorio() {
        estagio.submeterRelatorio(1, "Relatório mensal");
        estagio.avaliarRelatorio(1, StatusRelatorio.APROVADO);
        assertEquals(StatusRelatorio.APROVADO, estagio.getRelatorios().get(0).getStatus());
    }

    @Test
    void deveRejeitarRelatorio() {
        estagio.submeterRelatorio(1, "Relatório mensal");
        estagio.avaliarRelatorio(1, StatusRelatorio.REJEITADO);
        assertEquals(StatusRelatorio.REJEITADO, estagio.getRelatorios().get(0).getStatus());
    }

    @Test
    void deveRejeitarAvaliacaoComStatusPendente() {
        estagio.submeterRelatorio(1, "Relatório mensal");
        var ex = assertThrows(IllegalArgumentException.class,
                () -> estagio.avaliarRelatorio(1, StatusRelatorio.PENDENTE));
        assertTrue(ex.getMessage().contains("RN-9"));
    }

    @Test
    void deveRejeitarAvaliacaoDeRelatorioJaAvaliado() {
        estagio.submeterRelatorio(1, "Relatório mensal");
        estagio.avaliarRelatorio(1, StatusRelatorio.APROVADO);
        var ex = assertThrows(IllegalStateException.class,
                () -> estagio.avaliarRelatorio(1, StatusRelatorio.REJEITADO));
        assertTrue(ex.getMessage().contains("RN-10"));
    }

    @Test
    void deveSolicitarEncerramento() {
        estagio.solicitarEncerramento();
        assertEquals(StatusEstagio.ENCERRAMENTO_SOLICITADO, estagio.getStatus());
    }

    @Test
    void deveRejeitarSolicitacaoEncerramentoQuandoNaoEmAndamento() {
        estagio.solicitarEncerramento();
        var ex = assertThrows(IllegalStateException.class, () -> estagio.solicitarEncerramento());
        assertTrue(ex.getMessage().contains("RN-11"));
    }

    @Test
    void deveHomologarEncerramento() {
        estagio.solicitarEncerramento();
        estagio.homologarEncerramento(coordenadorId);
        assertEquals(StatusEstagio.ENCERRADO, estagio.getStatus());
    }

    @Test
    void deveRejeitarHomologacaoSemSolicitacao() {
        var ex = assertThrows(IllegalStateException.class,
                () -> estagio.homologarEncerramento(coordenadorId));
        assertTrue(ex.getMessage().contains("RN-12"));
    }
}
