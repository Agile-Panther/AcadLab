package school.cesar.acadlab.dominio.apoiopsicopedagogico.solicitacao;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class SolicitacaoApoioId {
    private final int id;

    public SolicitacaoApoioId(int id) {
        isTrue(id > 0, "O id deve ser positivo");
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof SolicitacaoApoioId) {
            var solicitacaoApoioId = (SolicitacaoApoioId) obj;
            return id == solicitacaoApoioId.id;
        }
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return Integer.toString(id); }
}
