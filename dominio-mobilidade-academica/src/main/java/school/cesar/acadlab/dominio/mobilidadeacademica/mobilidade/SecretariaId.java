package school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class SecretariaId {
    private final int id;

    public SecretariaId(int id) {
        isTrue(id > 0, "O id da secretaria deve ser positivo");
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SecretariaId other) {
            return id == other.id;
        }
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return Integer.toString(id); }
}
