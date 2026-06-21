package school.cesar.acadlab.dominio.apoiopsicopedagogico.caso;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class CasoId {
    private final int id;

    public CasoId(int id) {
        isTrue(id > 0, "O id deve ser positivo");
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof CasoId) {
            var casoId = (CasoId) obj;
            return id == casoId.id;
        }
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return Integer.toString(id); }
}
