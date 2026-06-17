package school.cesar.acadlab.dominio.integralizacao.colacao;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.integralizacao.EstudanteId;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoId;

class ColacaoDeGrauTest {

    private final ColacaoId colacaoId = new ColacaoId(1);
    private final EstudanteId estudanteId = new EstudanteId(1);
    private final IntegralizacaoId integralizacaoId = new IntegralizacaoId(1);
    private final LocalDate dataAptidao = LocalDate.of(2026, 6, 1);

    private ColacaoDeGrau criarColacao() {
        return new ColacaoDeGrau(colacaoId, estudanteId, integralizacaoId, dataAptidao);
    }

    @Test
    void registrar_comDataPosteriorAAprovacao_deveRegistrarComSucesso() {
        var colacao = criarColacao();
        var dataCerimonia = dataAptidao.plusDays(30);

        var evento = colacao.registrar(dataCerimonia, "Auditório Central");

        assertNotNull(evento);
        assertEquals(dataCerimonia, colacao.getDataCerimonia());
        assertEquals("Auditório Central", colacao.getLocal());
    }

    @Test
    void registrar_comDataIgualAAprovacao_deveRegistrarComSucesso() {
        var colacao = criarColacao();

        var evento = colacao.registrar(dataAptidao, "Auditório Central");

        assertNotNull(evento);
        assertEquals(dataAptidao, colacao.getDataCerimonia());
    }

    @Test
    void registrar_comDataAnteriorAAprovacao_deveLancarExcecao() {
        var colacao = criarColacao();
        var dataAnterior = dataAptidao.minusDays(1);

        var excecao = assertThrows(IllegalArgumentException.class,
                () -> colacao.registrar(dataAnterior, "Auditório Central"));
        assertTrue(excecao.getMessage().contains("RN8"));
    }

    @Test
    void registrar_comLocalEmBranco_deveLancarExcecao() {
        var colacao = criarColacao();

        assertThrows(IllegalArgumentException.class,
                () -> colacao.registrar(dataAptidao.plusDays(30), ""));
    }

    @Test
    void registrar_comDataNula_deveLancarExcecao() {
        var colacao = criarColacao();

        assertThrows(NullPointerException.class,
                () -> colacao.registrar(null, "Auditório Central"));
    }

    @Test
    void reconstituir_deveRecuperarEstadoCompleto() {
        var dataCerimonia = dataAptidao.plusDays(30);
        var colacao = ColacaoDeGrau.reconstituir(
                colacaoId, estudanteId, integralizacaoId, dataAptidao,
                dataCerimonia, "Auditório Central");

        assertEquals(colacaoId, colacao.getId());
        assertEquals(estudanteId, colacao.getEstudanteId());
        assertEquals(dataCerimonia, colacao.getDataCerimonia());
        assertEquals("Auditório Central", colacao.getLocal());
    }
}
