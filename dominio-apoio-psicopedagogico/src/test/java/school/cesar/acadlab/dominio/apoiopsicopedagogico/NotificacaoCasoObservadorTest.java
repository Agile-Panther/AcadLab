package school.cesar.acadlab.dominio.apoiopsicopedagogico;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;
import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.evento.EventoObservador;
import school.cesar.acadlab.dominio.evento.Notificacao;
import school.cesar.acadlab.dominio.evento.RegistroNotificacoes;

class NotificacaoCasoObservadorTest {

    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 3, 10, 9, 0);

    private Caso caso() {
        return new Caso(new CasoId(1), new EstudanteId(5));
    }

    @Test
    void aoAgendarAtendimento_observadorNotificaEstudante() {
        var registro = new RegistroEmMemoria();
        var barramento = new BarramentoDespachante();
        barramento.adicionar(new NotificacaoCasoObservador(registro));

        barramento.postar(caso().agendar(AGORA.plusDays(1), AGORA));

        assertEquals(1, registro.todas().size());
        Notificacao notificacao = registro.todas().get(0);
        assertEquals(5, notificacao.destinatarioId());
        assertEquals("APOIO_AGENDAMENTO_MARCADO", notificacao.tipo());
    }

    @Test
    void notificacao_respeitaSigilo_naoExpoeDetalhesClinicos() {
        var registro = new RegistroEmMemoria();
        var barramento = new BarramentoDespachante();
        barramento.adicionar(new NotificacaoCasoObservador(registro));

        barramento.postar(caso().agendar(AGORA.plusDays(1), AGORA));

        String mensagem = registro.todas().get(0).mensagem().toLowerCase();
        assertTrue(mensagem.contains("agendado"));
        assertTrue(registro.doDestinatario(5).size() == 1);
    }

    @Test
    void observador_ignoraEventosDeOutroTipo() {
        var registro = new RegistroEmMemoria();
        var barramento = new BarramentoDespachante();
        barramento.adicionar(new NotificacaoCasoObservador(registro));

        barramento.postar("evento sem relação com apoio psicopedagógico");

        assertTrue(registro.todas().isEmpty());
    }

    @Test
    void falhaDoObservador_naoPropagaParaQuemPublica() {
        var barramento = new BarramentoDespachante();
        barramento.adicionar(new NotificacaoCasoObservador(new RegistroQueFalha()));

        assertDoesNotThrow(() -> barramento.postar(caso().agendar(AGORA.plusDays(1), AGORA)));
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
