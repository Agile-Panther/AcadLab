package school.cesar.acadlab.dominio.secretariavirtual.protocolo;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class ProtocoloId {
    private final int id;

    public ProtocoloId(int id) {
        isTrue(id > 0, "O id deve ser positivo");
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ProtocoloId) {
            var protocoloId = (ProtocoloId) obj;
            return id == protocoloId.id;
        }
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return Integer.toString(id); }
}
