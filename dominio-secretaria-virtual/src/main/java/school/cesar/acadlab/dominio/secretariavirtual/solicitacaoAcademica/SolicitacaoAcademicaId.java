package school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class SolicitacaoAcademicaId {
    private final int id;

    public SolicitacaoAcademicaId(int id) {
        isTrue(id > 0, "O id deve ser positivo");
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof SolicitacaoAcademicaId) {
            var solicitacaoId = (SolicitacaoAcademicaId) obj;
            return id == solicitacaoId.id;
        }
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return Integer.toString(id); }
}
