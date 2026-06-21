package school.cesar.acadlab.dominio.atividadescomplementares;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import school.cesar.acadlab.dominio.atividadescomplementares.atividade.AtividadeComplementar;
import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.evento.EventoObservador;
import school.cesar.acadlab.dominio.evento.Notificacao;
import school.cesar.acadlab.dominio.evento.RegistroNotificacoes;

class NotificacaoAtividadeObservadorTest {

    private AtividadeComplementar atividade() {
        return new AtividadeComplementar(new AtividadeComplementarId(1), new EstudanteId(7),
                new CategoriaAtividadeId(1), "CERT-1", "Curso de extensão", 30, LocalDate.of(2025, 1, 10));
    }

    @Test
    void aoDeferirAtividade_observadorNotificaEstudante() {
        var registro = new RegistroEmMemoria();
        var barramento = new BarramentoDespachante();
        barramento.adicionar(new NotificacaoAtividadeObservador(registro));

        barramento.postar(atividade().deferir(20));

        assertEquals(1, registro.todas().size());
        Notificacao notificacao = registro.todas().get(0);
        assertEquals(7, notificacao.destinatarioId());
        assertEquals("ATIVIDADE_DEFERIDA", notificacao.tipo());
        assertTrue(notificacao.mensagem().contains("20 horas"));
    }

    @Test
    void observador_ignoraEventosDeOutroTipo() {
        var registro = new RegistroEmMemoria();
        var barramento = new BarramentoDespachante();
        barramento.adicionar(new NotificacaoAtividadeObservador(registro));

        barramento.postar("evento sem relação com atividades");

        assertTrue(registro.todas().isEmpty());
    }

    @Test
    void falhaDoObservador_naoPropagaParaQuemPublica() {
        var barramento = new BarramentoDespachante();
        barramento.adicionar(new NotificacaoAtividadeObservador(new RegistroQueFalha()));

        // A falha do efeito do observador não pode invalidar a operação principal.
        assertDoesNotThrow(() -> barramento.postar(atividade().deferir(20)));
    }

    /** Barramento de teste que efetivamente registra e despacha aos observadores. */
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
