package school.cesar.acadlab.dominio.curriculo;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public final class MatrizCurricularId {
    private final int valor;

    public MatrizCurricularId(int valor) {
        isTrue(valor > 0, "MatrizCurricularId deve ser positivo");
        this.valor = valor;
    }

    public int getValor() { return valor; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MatrizCurricularId)) return false;
        MatrizCurricularId other = (MatrizCurricularId) obj;
        return valor == other.valor;
    }

    @Override
    public int hashCode() { return Objects.hash(valor); }

    @Override
    public String toString() { return Integer.toString(valor); }
}
