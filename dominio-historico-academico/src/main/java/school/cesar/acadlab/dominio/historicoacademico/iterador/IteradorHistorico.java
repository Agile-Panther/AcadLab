package school.cesar.acadlab.dominio.historicoacademico.iterador;

public interface IteradorHistorico<T> {
    boolean temProximo();
    T proximo();
}
