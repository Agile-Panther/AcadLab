package school.cesar.acadlab.dominio.matricula.matricula;

import static org.apache.commons.lang3.Validate.isTrue;

public class MatriculaId {
    private final int id;

    public MatriculaId(int id) {
        isTrue(id > 0, "O id da matrícula deve ser positivo");
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatriculaId)) return false;
        return id == ((MatriculaId) o).id;
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }

    @Override
    public String toString() { return "MatriculaId{" + id + "}"; }
}
