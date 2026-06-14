package school.cesar.acadlab.dominio.matricula.matricula;

import static org.apache.commons.lang3.Validate.isTrue;

public class DisciplinaId {
    private final int id;

    public DisciplinaId(int id) {
        isTrue(id > 0, "O id da disciplina deve ser positivo");
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisciplinaId)) return false;
        return id == ((DisciplinaId) o).id;
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }

    @Override
    public String toString() { return "DisciplinaId{" + id + "}"; }
}
