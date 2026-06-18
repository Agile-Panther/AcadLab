package school.cesar.acadlab.dominio.permanenciaacademica;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class BeneficioId {
    private final int valor;

    public BeneficioId(int valor) {
        isTrue(valor > 0, "O id deve ser positivo");
        this.valor = valor;
    }

    public int getValor() { return valor; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BeneficioId other) return valor == other.valor;
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return Integer.toString(valor); }
}
