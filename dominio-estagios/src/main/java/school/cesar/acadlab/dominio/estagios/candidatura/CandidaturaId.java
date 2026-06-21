package school.cesar.acadlab.dominio.estagios.candidatura;

import java.util.Objects;
import static org.apache.commons.lang3.Validate.isTrue;

public class CandidaturaId {
    private final int valor;

    public CandidaturaId(int valor) {
        isTrue(valor > 0, "Id da candidatura deve ser positivo");
        this.valor = valor;
    }

    public int getValor() { return valor; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CandidaturaId)) return false;
        return valor == ((CandidaturaId) o).valor;
    }

    @Override public int hashCode() { return Objects.hash(valor); }
}
