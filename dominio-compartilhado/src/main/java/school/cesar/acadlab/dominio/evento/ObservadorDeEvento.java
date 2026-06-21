package school.cesar.acadlab.dominio.evento;

import java.util.Objects;

/**
 * Base para observadores concretos do barramento de eventos.
 *
 * <p>O barramento entrega todos os eventos a todos os observadores registrados;
 * esta base filtra pelo tipo de evento de interesse e isola falhas: uma exceção
 * dentro de {@link #reagir} não propaga, de modo que a falha de um observador não
 * invalida a operação principal que já foi concluída.
 *
 * @param <E> tipo de evento ao qual o observador reage
 */
public abstract class ObservadorDeEvento<E> implements EventoObservador<Object> {

    private final Class<E> tipoEvento;

    protected ObservadorDeEvento(Class<E> tipoEvento) {
        this.tipoEvento = Objects.requireNonNull(tipoEvento, "O tipo do evento não pode ser nulo");
    }

    @Override
    public final void observarEvento(Object evento) {
        if (tipoEvento.isInstance(evento)) {
            try {
                reagir(tipoEvento.cast(evento));
            } catch (RuntimeException falhaDoObservador) {
                // Efeito colateral do observador não pode reverter a operação principal.
            }
        }
    }

    protected abstract void reagir(E evento);
}
