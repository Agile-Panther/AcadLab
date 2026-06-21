package school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class PsicopedagogoId {
    private final int id;

    public PsicopedagogoId(int id) {
        isTrue(id > 0, "O id deve ser positivo");
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof PsicopedagogoId) {
            var psicopedagogoId = (PsicopedagogoId) obj;
            return id == psicopedagogoId.id;
        }
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return Integer.toString(id); }
}
