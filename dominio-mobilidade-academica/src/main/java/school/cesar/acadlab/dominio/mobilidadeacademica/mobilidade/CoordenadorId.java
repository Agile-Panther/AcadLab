package school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class CoordenadorId {
    private final int id;

    public CoordenadorId(int id) {
        isTrue(id > 0, "O id do coordenador deve ser positivo");
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CoordenadorId other) {
            return id == other.id;
        }
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return Integer.toString(id); }
}
