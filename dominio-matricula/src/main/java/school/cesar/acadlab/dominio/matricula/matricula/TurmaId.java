package school.cesar.acadlab.dominio.matricula.matricula;

import static org.apache.commons.lang3.Validate.isTrue;

public class TurmaId {
    private final int id;

    public TurmaId(int id) {
        isTrue(id > 0, "O id da turma deve ser positivo");
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TurmaId)) return false;
        return id == ((TurmaId) o).id;
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }

    @Override
    public String toString() { return "TurmaId{" + id + "}"; }
}
