package school.cesar.acadlab.dominio.gestaofinanceira;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.evento.EventoObservador;
import school.cesar.acadlab.dominio.evento.Notificacao;
import school.cesar.acadlab.dominio.evento.RegistroNotificacoes;
import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.Cobranca;

class NotificacaoCobrancaObservadorTest {

    private final EstudanteId titular = new EstudanteId(3);

    private Cobranca cobranca() {
        return new Cobranca(new CobrancaId(1), new ContratoId(1), titular,
                new PeriodoLetivoId(1), new BigDecimal("1000.00"), LocalDate.of(2026, 1, 10));
    }

    @Test
    void aoContestarCobranca_observadorNotificaEstudante() {
        var registro = new RegistroEmMemoria();
        var barramento = new BarramentoDespachante();
        barramento.adicionar(new NotificacaoCobrancaObservador(registro));

        barramento.postar(cobranca().contestar(titular, "valor divergente", LocalDate.of(2026, 1, 12)));

        assertEquals(1, registro.todas().size());
        Notificacao notificacao = registro.todas().get(0);
        assertEquals(3, notificacao.destinatarioId());
        assertEquals("COBRANCA_CONTESTACAO_REGISTRADA", notificacao.tipo());
    }

    @Test
    void observador_ignoraEventosDeOutroTipo() {
        var registro = new RegistroEmMemoria();
        var barramento = new BarramentoDespachante();
        barramento.adicionar(new NotificacaoCobrancaObservador(registro));

        barramento.postar("evento sem relação com cobranças");

        assertTrue(registro.todas().isEmpty());
    }

    @Test
    void falhaDoObservador_naoPropagaParaQuemPublica() {
        var barramento = new BarramentoDespachante();
        barramento.adicionar(new NotificacaoCobrancaObservador(new RegistroQueFalha()));

        assertDoesNotThrow(() -> barramento.postar(
                cobranca().contestar(titular, "valor divergente", LocalDate.of(2026, 1, 12))));
    }

    private static final class BarramentoDespachante implements EventoBarramento {
        private final List<EventoObservador<Object>> observadores = new ArrayList<>();

        @Override
        @SuppressWarnings("unchecked")
        public <E> void adicionar(EventoObservador<E> observador) {
            observadores.add((EventoObservador<Object>) observador);
        }

        @Override
        public <E> void postar(E evento) {
            observadores.forEach(o -> o.observarEvento(evento));
        }
    }

    private static final class RegistroEmMemoria implements RegistroNotificacoes {
        private final List<Notificacao> notificacoes = new ArrayList<>();

        @Override
        public void registrar(Notificacao notificacao) {
            notificacoes.add(notificacao);
        }

        @Override
        public List<Notificacao> todas() {
            return List.copyOf(notificacoes);
        }

        @Override
        public List<Notificacao> doDestinatario(int destinatarioId) {
            return notificacoes.stream().filter(n -> n.destinatarioId() == destinatarioId).toList();
        }
    }

    private static final class RegistroQueFalha implements RegistroNotificacoes {
        @Override
        public void registrar(Notificacao notificacao) {
            throw new RuntimeException("falha simulada ao registrar notificação");
        }

        @Override
        public List<Notificacao> todas() {
            return List.of();
        }

        @Override
        public List<Notificacao> doDestinatario(int destinatarioId) {
            return List.of();
        }
    }
}
