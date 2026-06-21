package school.cesar.acadlab.dominio.matricula;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import school.cesar.acadlab.dominio.matricula.matricula.EstrategiaMatricula;
import school.cesar.acadlab.dominio.matricula.matricula.ValidacaoExcecao;
import school.cesar.acadlab.dominio.matricula.matricula.ValidacaoRegular;

/**
 * Demonstra a intercambialidade do padrão Strategy de validação de matrícula:
 * a MESMA entrada produz comportamentos distintos conforme a estratégia escolhida,
 * mantendo o mesmo contrato {@link EstrategiaMatricula}.
 */
class EstrategiaMatriculaTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 3, 1);
    private static final LocalDate FIM = LocalDate.of(2026, 3, 31);
    private static final LocalDate HOJE = LocalDate.of(2026, 3, 10);

    private final EstrategiaMatricula regular = new ValidacaoRegular();
    private final EstrategiaMatricula excecao = new ValidacaoExcecao();

    @Test
    void preRequisitoNaoCumprido_regularRejeita_excecaoAceita() {
        // mesma entrada (pré-requisito não cumprido), estratégias diferentes
        var erro = assertThrows(IllegalArgumentException.class,
                () -> regular.validarAdicao(0, 4, 20, false, true, false, HOJE, INICIO, FIM));
        assertTrue(erro.getMessage().contains("pré-requisitos não cumpridos"));

        assertDoesNotThrow(
                () -> excecao.validarAdicao(0, 4, 20, false, true, false, HOJE, INICIO, FIM));
    }

    @Test
    void pendenciasAcademicas_regularRejeita_excecaoAceita() {
        var erro = assertThrows(IllegalArgumentException.class,
                () -> regular.validarAdicao(0, 4, 20, true, true, true, HOJE, INICIO, FIM));
        assertTrue(erro.getMessage().contains("pendências"));

        assertDoesNotThrow(
                () -> excecao.validarAdicao(0, 4, 20, true, true, true, HOJE, INICIO, FIM));
    }

    @Test
    void correquisitoAusente_regularRejeita_excecaoAceita() {
        assertThrows(IllegalArgumentException.class,
                () -> regular.validarAdicao(0, 4, 20, true, false, false, HOJE, INICIO, FIM));

        assertDoesNotThrow(
                () -> excecao.validarAdicao(0, 4, 20, true, false, false, HOJE, INICIO, FIM));
    }

    @Test
    void foraDaJanela_ambasEstrategiasRejeitam() {
        // regra comum: nenhuma estratégia permite matrícula fora da janela
        LocalDate foraDaJanela = FIM.plusDays(1);

        assertThrows(IllegalArgumentException.class,
                () -> regular.validarAdicao(0, 4, 20, true, true, false, foraDaJanela, INICIO, FIM));
        assertThrows(IllegalArgumentException.class,
                () -> excecao.validarAdicao(0, 4, 20, true, true, false, foraDaJanela, INICIO, FIM));
    }

    @Test
    void limiteDeCreditosExcedido_ambasEstrategiasRejeitam() {
        // regra comum: nem a exceção deferida ultrapassa o limite de créditos
        assertThrows(IllegalArgumentException.class,
                () -> regular.validarAdicao(18, 4, 20, true, true, false, HOJE, INICIO, FIM));
        assertThrows(IllegalArgumentException.class,
                () -> excecao.validarAdicao(18, 4, 20, true, true, false, HOJE, INICIO, FIM));
    }
}
