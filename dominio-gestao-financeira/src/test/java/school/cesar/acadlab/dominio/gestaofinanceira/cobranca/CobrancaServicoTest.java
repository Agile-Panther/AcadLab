package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.gestaofinanceira.*;
import school.cesar.acadlab.dominio.evento.EventoBarramento;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CobrancaServicoTest {

    private CobrancaRepositorio repositorio;
    private VerificadorMatriculaConfirmada verificadorMatricula;
    private VerificadorAutorizacaoDesconto verificadorAutorizacao;
    private EventoBarramento barramento;
    private CobrancaServico servico;

    @BeforeEach
    void setUp() {
        repositorio = mock(CobrancaRepositorio.class);
        verificadorMatricula = mock(VerificadorMatriculaConfirmada.class);
        verificadorAutorizacao = mock(VerificadorAutorizacaoDesconto.class);
        barramento = mock(EventoBarramento.class);
        servico = new CobrancaServico(repositorio, verificadorMatricula, verificadorAutorizacao, barramento);
        when(repositorio.proximoId()).thenReturn(new CobrancaId(1));
    }

    @Test
    void gerarCobranca_comMatriculaConfirmada_deveSalvar() {
        var estudanteId = new EstudanteId(1);
        var periodoId = new PeriodoLetivoId(1);
        when(verificadorMatricula.possuiMatricula(estudanteId, periodoId)).thenReturn(true);

        var resultado = servico.gerarCobranca(new ContratoId(1), estudanteId, periodoId,
                new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));

        verify(repositorio).salvar(any());
        assertEquals(StatusCobranca.ABERTA, resultado.getStatus());
    }

    @Test
    void gerarCobranca_semMatricula_deveLancarExcecaoSemSalvar() {
        var estudanteId = new EstudanteId(1);
        var periodoId = new PeriodoLetivoId(1);
        when(verificadorMatricula.possuiMatricula(estudanteId, periodoId)).thenReturn(false);

        assertThrows(IllegalStateException.class, () ->
                servico.gerarCobranca(new ContratoId(1), estudanteId, periodoId,
                        new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10)));
        verify(repositorio, never()).salvar(any());
    }

    @Test
    void aplicarDesconto_comAutorizacaoValida_deveSalvar() {
        when(verificadorAutorizacao.autorizacaoValida("AUTH-001")).thenReturn(true);
        var cobranca = criarCobrancaAberta();
        when(repositorio.obter(cobranca.getId())).thenReturn(cobranca);

        servico.aplicarDesconto(cobranca.getId(), new BigDecimal("10"), "AUTH-001");

        verify(repositorio).salvar(cobranca);
    }

    @Test
    void aplicarDesconto_comAutorizacaoInvalida_deveLancarExcecaoSemBuscar() {
        when(verificadorAutorizacao.autorizacaoValida("AUTH-INVALIDA")).thenReturn(false);

        assertThrows(IllegalStateException.class, () ->
                servico.aplicarDesconto(new CobrancaId(1), new BigDecimal("10"), "AUTH-INVALIDA"));
        verify(repositorio, never()).obter(any());
    }

    @Test
    void emitirComprovante_comPagamentoConfirmado_deveRetornar() {
        var cobranca = criarCobrancaAberta();
        cobranca.registrarPagamento(new BigDecimal("1500.00"), LocalDate.now(), "PAG-001");
        when(repositorio.obter(cobranca.getId())).thenReturn(cobranca);

        var resultado = servico.emitirComprovante(cobranca.getId());

        assertNotNull(resultado);
        assertEquals(StatusPagamento.CONFIRMADO, resultado.getStatus());
    }

    @Test
    void emitirComprovante_semPagamentoConfirmado_deveLancarExcecao() {
        var cobranca = criarCobrancaAberta();
        when(repositorio.obter(cobranca.getId())).thenReturn(cobranca);

        assertThrows(IllegalStateException.class, () -> servico.emitirComprovante(cobranca.getId()));
    }

    @Test
    void cancelarPagamento_deveSalvarComCobrancaReativada() {
        var cobranca = criarCobrancaAberta();
        cobranca.registrarPagamento(new BigDecimal("1500.00"), LocalDate.now(), "PAG-001");
        when(repositorio.obter(cobranca.getId())).thenReturn(cobranca);

        servico.cancelarPagamento(cobranca.getId(), "Estorno", "operador@cesar.school");

        verify(repositorio).salvar(cobranca);
        assertEquals(StatusCobranca.ABERTA, cobranca.getStatus());
        assertEquals(StatusPagamento.CANCELADO, cobranca.getPagamento().getStatus());
    }

    @Test
    void contestar_devePublicarEvento() {
        var cobranca = criarCobrancaAberta();
        when(repositorio.obter(cobranca.getId())).thenReturn(cobranca);

        servico.contestar(cobranca.getId(), new EstudanteId(1), "Valor diverge do contrato");

        verify(barramento).postar(any(Cobranca.ContestacaoRegistradaEvento.class));
    }

    @Test
    void registrarPagamento_devePublicarEvento() {
        var cobranca = criarCobrancaAberta();
        when(repositorio.obter(cobranca.getId())).thenReturn(cobranca);

        servico.registrarPagamento(cobranca.getId(), new BigDecimal("1500.00"), LocalDate.now(), "PAG-001");

        verify(barramento).postar(any(Cobranca.PagamentoRegistradoEvento.class));
    }

    private Cobranca criarCobrancaAberta() {
        return new Cobranca(new CobrancaId(1), new ContratoId(1), new EstudanteId(1),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
    }
}
