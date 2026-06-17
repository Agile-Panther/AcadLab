package school.cesar.acadlab.dominio.ofertaturmas.turma.decorator;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public final class EstudanteId {
    private final int valor;

    public EstudanteId(int valor) {
        isTrue(valor > 0, "O id deve ser positivo");
        this.valor = valor;
    }

    public int getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        return o instanceof EstudanteId e && this.valor == e.valor;
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return "EstudanteId(" + valor + ")"; }
}
