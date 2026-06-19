package school.cesar.acadlab.dominio.estagios.oportunidade;

import java.util.Objects;
import static org.apache.commons.lang3.Validate.isTrue;

public class SetorEstagiosId {
    private final int valor;

    public SetorEstagiosId(int valor) {
        isTrue(valor > 0, "Id do setor de estágios deve ser positivo");
        this.valor = valor;
    }

    public int getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SetorEstagiosId)) return false;
        return valor == ((SetorEstagiosId) o).valor;
    }

    @Override public int hashCode() { return Objects.hash(valor); }
}
