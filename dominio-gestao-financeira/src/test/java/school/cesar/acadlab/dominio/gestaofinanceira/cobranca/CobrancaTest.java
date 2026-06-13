package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.gestaofinanceira.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class CobrancaTest {

    private Cobranca criarCobranca() {
        return new Cobranca(new CobrancaId(1), new ContratoId(1), new EstudanteId(1),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
    }

    @Test
    void novaCobranca_deveIniciarComStatusAberta() {
        assertEquals(StatusCobranca.ABERTA, criarCobranca().getStatus());
    }

    @Test
    void novaCobranca_deveIniciarComVersao1() {
        assertEquals(1, criarCobranca().getVersao());
    }

    @Test
    void novaCobranca_valorAtualDeveIgualValorBase() {
        var cobranca = criarCobranca();
        assertEquals(0, cobranca.getValorBase().compareTo(cobranca.getValorAtual()));
    }

    @Test
    void contestar_deveRegistrarContestacaoEMudarStatus() {
        var cobranca = criarCobranca();
        cobranca.contestar(new EstudanteId(1), "Valor incorreto", LocalDate.now());
        assertEquals(StatusCobranca.CONTESTADA, cobranca.getStatus());
        assertNotNull(cobranca.getContestacao());
        assertEquals(StatusContestacao.PENDENTE, cobranca.getContestacao().getStatus());
    }

    @Test
    void contestar_comEstudanteErrado_deveLancarExcecao() {
        var cobranca = criarCobranca();
        assertThrows(IllegalStateException.class, () ->
                cobranca.contestar(new EstudanteId(99), "motivo", LocalDate.now()));
    }

    @Test
    void contestar_comContestacaoPendente_deveLancarExcecao() {
        var cobranca = criarCobranca();
        cobranca.contestar(new EstudanteId(1), "primeira", LocalDate.now());
        assertThrows(IllegalStateException.class, () ->
                cobranca.contestar(new EstudanteId(1), "segunda", LocalDate.now()));
    }

    @Test
    void resolverContestacao_deveMudarStatusContestacaoParaResolvida() {
        var cobranca = criarCobranca();
        cobranca.contestar(new EstudanteId(1), "motivo", LocalDate.now());
        cobranca.resolverContestacao("Cobrado corretamente");
        assertEquals(StatusContestacao.RESOLVIDA, cobranca.getContestacao().getStatus());
        assertEquals(StatusCobranca.ABERTA, cobranca.getStatus());
    }

    @Test
    void aplicarDesconto_deveReduzirValorAtual() {
        var cobranca = criarCobranca();
        cobranca.aplicarDesconto(new BigDecimal("10"), "AUTH-001", LocalDate.now());
        assertEquals(0, new BigDecimal("1350.00").compareTo(cobranca.getValorAtual()));
    }

    @Test
    void aplicarDesconto_deveAdicionarNoHistoricoDeDescontos() {
        var cobranca = criarCobranca();
        cobranca.aplicarDesconto(new BigDecimal("10"), "AUTH-001", LocalDate.now());
        assertEquals(1, cobranca.getDescontos().size());
    }

    @Test
    void registrarPagamento_deveDefinirStatusPaga() {
        var cobranca = criarCobranca();
        cobranca.registrarPagamento(new BigDecimal("1500.00"), LocalDate.now(), "PAG-001");
        assertEquals(StatusCobranca.PAGA, cobranca.getStatus());
    }

    @Test
    void registrarPagamento_comStatusCancelada_deveLancarExcecao() {
        var cobranca = criarCobranca();
        cobranca.cancelar("motivo");
        assertThrows(IllegalStateException.class, () ->
                cobranca.registrarPagamento(new BigDecimal("1500.00"), LocalDate.now(), "PAG-001"));
    }

    @Test
    void cancelarPagamento_deveMudarPagamentoParaCancelado() {
        var cobranca = criarCobranca();
        cobranca.registrarPagamento(new BigDecimal("1500.00"), LocalDate.now(), "PAG-001");
        cobranca.cancelarPagamento("Estorno solicitado", "operador@cesar.school");
        assertEquals(StatusPagamento.CANCELADO, cobranca.getPagamento().getStatus());
    }

    @Test
    void cancelarPagamento_deveReativarCobrancaParaAberta() {
        var cobranca = criarCobranca();
        cobranca.registrarPagamento(new BigDecimal("1500.00"), LocalDate.now(), "PAG-001");
        cobranca.cancelarPagamento("Estorno solicitado", "operador@cesar.school");
        assertEquals(StatusCobranca.ABERTA, cobranca.getStatus());
    }

    @Test
    void gerarNovaVersao_deveIncrementarVersaoEGravarHistorico() {
        var cobranca = criarCobranca();
        cobranca.gerarNovaVersao("Reajuste", new BigDecimal("1600.00"));
        assertEquals(2, cobranca.getVersao());
        assertEquals(1, cobranca.getHistorico().size());
        assertEquals(0, new BigDecimal("1600.00").compareTo(cobranca.getValorAtual()));
    }
}
