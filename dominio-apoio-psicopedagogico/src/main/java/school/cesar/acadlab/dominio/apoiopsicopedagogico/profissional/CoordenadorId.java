package school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class CoordenadorId {
    private final int id;

    public CoordenadorId(int id) {
        isTrue(id > 0, "O id deve ser positivo");
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof CoordenadorId) {
            var coordenadorId = (CoordenadorId) obj;
            return id == coordenadorId.id;
        }
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return Integer.toString(id); }
}
