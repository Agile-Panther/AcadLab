package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
import java.math.BigDecimal;
import java.time.LocalDate;

class BolsaTest {
    private Bolsa nova() {
        return Bolsa.conceder(new BolsaId(1), new EstudanteId(1), TipoBolsa.MERITO,
                new BigDecimal("50"), LocalDate.of(2025, 12, 31));
    }

    @Test void concedeAtiva() {
        var b = nova();
        assertEquals(StatusBolsa.ATIVA, b.getStatus());
        assertEquals(0, new BigDecimal("50").compareTo(b.getPercentual()));
    }

    @Test void percentualForaDoIntervaloRejeitado() {
        assertThrows(IllegalArgumentException.class, () -> Bolsa.conceder(new BolsaId(1),
                new EstudanteId(1), TipoBolsa.MERITO, new BigDecimal("150"), LocalDate.of(2025, 12, 31)));
        assertThrows(IllegalArgumentException.class, () -> Bolsa.conceder(new BolsaId(1),
                new EstudanteId(1), TipoBolsa.MERITO, new BigDecimal("-1"), LocalDate.of(2025, 12, 31)));
    }

    @Test void validadeNulaRejeitada() {
        assertThrows(IllegalArgumentException.class, () -> Bolsa.conceder(new BolsaId(1),
                new EstudanteId(1), TipoBolsa.MERITO, new BigDecimal("50"), null));
    }

    @Test void suspenderEReativar() {
        var b = nova();
        b.suspender();
        assertEquals(StatusBolsa.SUSPENSA, b.getStatus());
        b.reativar();
        assertEquals(StatusBolsa.ATIVA, b.getStatus());
    }

    @Test void naoSuspendeJaSuspensa() {
        var b = nova();
        b.suspender();
        assertThrows(IllegalStateException.class, b::suspender);
    }

    @Test void naoReativaQuemNaoEstaSuspensa() {
        var b = nova();
        assertThrows(IllegalStateException.class, b::reativar);
    }

    @Test void solicitarRenovacaoExigeAtiva() {
        var b = nova();
        b.solicitarRenovacao();
        assertEquals(StatusBolsa.EM_RENOVACAO, b.getStatus());
        assertThrows(IllegalStateException.class, b::solicitarRenovacao);
    }

    @Test void renovarEstendeValidadeEReativa() {
        var b = nova();
        b.solicitarRenovacao();
        b.renovar(LocalDate.of(2026, 12, 31));
        assertEquals(StatusBolsa.ATIVA, b.getStatus());
        assertEquals(LocalDate.of(2026, 12, 31), b.getValidade());
    }

    @Test void renovarRejeitaValidadeNaoPosterior() {
        var b = nova();
        assertThrows(IllegalArgumentException.class, () -> b.renovar(LocalDate.of(2024, 1, 1)));
    }

    @Test void naoRenovaSuspensa() {
        var b = nova();
        b.suspender();
        assertThrows(IllegalStateException.class, () -> b.renovar(LocalDate.of(2026, 12, 31)));
    }
}
