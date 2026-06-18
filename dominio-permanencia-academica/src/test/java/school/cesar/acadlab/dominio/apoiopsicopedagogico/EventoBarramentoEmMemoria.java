package school.cesar.acadlab.dominio.apoiopsicopedagogico;

import java.util.ArrayList;
import java.util.List;
import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.evento.EventoObservador;

public class EventoBarramentoEmMemoria implements EventoBarramento {
    private final List<Object> eventosPostados = new ArrayList<>();

    @Override
    public <E> void adicionar(EventoObservador<E> observador) {
        // no-op para testes
    }

    @Override
    public <E> void postar(E evento) {
        eventosPostados.add(evento);
    }

    public List<Object> getEventosPostados() { return eventosPostados; }

    public boolean foiPostado(Class<?> tipo) {
        return eventosPostados.stream().anyMatch(tipo::isInstance);
    }
}
