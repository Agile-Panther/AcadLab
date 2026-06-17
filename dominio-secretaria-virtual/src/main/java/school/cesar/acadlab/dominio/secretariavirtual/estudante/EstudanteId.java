package school.cesar.acadlab.dominio.secretariavirtual.estudante;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class EstudanteId {
    private final int id;

    public EstudanteId(int id) {
        isTrue(id > 0, "O id deve ser positivo");
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof EstudanteId) {
            var estudanteId = (EstudanteId) obj;
            return id == estudanteId.id;
        }
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return Integer.toString(id); }
}
