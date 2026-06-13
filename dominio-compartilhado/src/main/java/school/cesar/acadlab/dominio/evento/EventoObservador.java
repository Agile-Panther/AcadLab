package school.cesar.acadlab.dominio.evento;

public interface EventoObservador<E> {
    void observarEvento(E evento);
}
