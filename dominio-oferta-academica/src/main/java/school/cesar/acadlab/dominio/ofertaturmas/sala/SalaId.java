package school.cesar.acadlab.dominio.ofertaturmas.sala;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class SalaId {
    private final int id;

    public SalaId(int id) {
        isTrue(id > 0, "O id deve ser positivo");
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof SalaId) {
            return id == ((SalaId) obj).id;
        }
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return Integer.toString(id); }
}
