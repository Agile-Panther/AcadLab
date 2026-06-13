package school.cesar.acadlab.dominio.atividadescomplementares.atividade;

import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.atividadescomplementares.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class AtividadeComplementarTest {

    private AtividadeComplementar criarAtividade() {
        return new AtividadeComplementar(
            new AtividadeComplementarId(1), new EstudanteId(1), new CategoriaAtividadeId(1),
            "CERT-001", "Curso de Java", 40, LocalDate.of(2025, 3, 15));
    }

    @Test
    void novaAtividade_deveIniciarComStatusPendente() {
        assertEquals(StatusAtividade.PENDENTE, criarAtividade().getStatus());
    }

    @Test
    void deferir_comPendente_deveMudarParaDeferida() {
        var atividade = criarAtividade();
        atividade.deferir(30);
        assertEquals(StatusAtividade.DEFERIDA, atividade.getStatus());
    }

    @Test
    void deferir_deveDefinirHorasAprovadas() {
        var atividade = criarAtividade();
        atividade.deferir(30);
        assertEquals(30, atividade.getHorasAprovadas());
    }

    @Test
    void deferir_comHorasAcimaDoSubmetido_deveLancarExcecao() {
        var atividade = criarAtividade();
        assertThrows(IllegalArgumentException.class, () -> atividade.deferir(50));
    }

    @Test
    void deferir_comStatusIndeferido_deveLancarExcecao() {
        var atividade = criarAtividade();
        atividade.indeferir("motivo");
        assertThrows(IllegalStateException.class, () -> atividade.deferir(30));
    }

    @Test
    void indeferir_comPendente_deveMudarParaIndeferida() {
        var atividade = criarAtividade();
        atividade.indeferir("Certificado inválido");
        assertEquals(StatusAtividade.INDEFERIDA, atividade.getStatus());
    }

    @Test
    void indeferir_comStatusDeferido_deveLancarExcecao() {
        var atividade = criarAtividade();
        atividade.deferir(30);
        assertThrows(IllegalStateException.class, () -> atividade.indeferir("motivo"));
    }

    @Test
    void solicitarRevisao_comIndeferida_deveMudarParaRevisaoSolicitada() {
        var atividade = criarAtividade();
        atividade.indeferir("Certificado inválido");
        atividade.solicitarRevisao("Nova documentação");
        assertEquals(StatusAtividade.REVISAO_SOLICITADA, atividade.getStatus());
    }

    @Test
    void solicitarRevisao_comPendente_deveLancarExcecao() {
        var atividade = criarAtividade();
        assertThrows(IllegalStateException.class, () -> atividade.solicitarRevisao("motivo"));
    }

    @Test
    void deferir_aposRevisaoSolicitada_deveMudarParaDeferida() {
        var atividade = criarAtividade();
        atividade.indeferir("motivo");
        atividade.solicitarRevisao("justificativa");
        atividade.deferir(25);
        assertEquals(StatusAtividade.DEFERIDA, atividade.getStatus());
    }

    @Test
    void cancelar_comPendente_deveMudarParaCancelada() {
        var atividade = criarAtividade();
        atividade.cancelar();
        assertEquals(StatusAtividade.CANCELADA, atividade.getStatus());
    }

    @Test
    void cancelar_comDeferida_deveLancarExcecao() {
        var atividade = criarAtividade();
        atividade.deferir(30);
        assertThrows(IllegalStateException.class, () -> atividade.cancelar());
    }
}
