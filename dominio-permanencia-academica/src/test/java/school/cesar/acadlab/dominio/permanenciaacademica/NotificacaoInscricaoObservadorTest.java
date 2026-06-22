package school.cesar.acadlab.dominio.permanenciaacademica;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.evento.EventoObservador;
import school.cesar.acadlab.dominio.evento.Notificacao;
import school.cesar.acadlab.dominio.evento.RegistroNotificacoes;

class NotificacaoInscricaoObservadorTest {

    private Inscricao inscricao() {
        return new Inscricao(new InscricaoId(1), new EditalId(1), new EstudantePermanenciaId(9));
    }

    @Test
    void aoDeferirInscricao_observadorNotificaEstudante() {
        var registro = new RegistroEmMemoria();
        var barramento = new BarramentoDespachante();
        barramento.adicionar(new NotificacaoInscricaoObservador(registro));

        barramento.postar(inscricao().deferir(80));

        assertEquals(1, registro.todas().size());
        Notificacao notificacao = registro.todas().get(0);
        assertEquals(9, notificacao.destinatarioId());
        assertEquals("INSCRICAO_DEFERIDA", notificacao.tipo());
    }

    @Test
    void aoIndeferirInscricao_observadorNotificaEstudante() {
        var registro = new RegistroEmMemoria();
        var barramento = new BarramentoDespachante();
        barramento.adicionar(new NotificacaoInscricaoObservador(registro));

        barramento.postar(inscricao().indeferir());

        assertEquals("INSCRICAO_INDEFERIDA", registro.doDestinatario(9).get(0).tipo());
    }

    @Test
    void observador_ignoraEventosDeOutroTipo() {
        var registro = new RegistroEmMemoria();
        var barramento = new BarramentoDespachante();
        barramento.adicionar(new NotificacaoInscricaoObservador(registro));

        barramento.postar("evento sem relação com permanência");

        assertTrue(registro.todas().isEmpty());
    }

    @Test
    void falhaDoObservador_naoPropagaParaQuemPublica() {
        var barramento = new BarramentoDespachante();
        barramento.adicionar(new NotificacaoInscricaoObservador(new RegistroQueFalha()));

        assertDoesNotThrow(() -> barramento.postar(inscricao().deferir(80)));
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
