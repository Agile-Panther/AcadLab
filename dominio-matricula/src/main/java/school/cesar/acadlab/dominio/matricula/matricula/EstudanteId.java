package school.cesar.acadlab.dominio.matricula.matricula;

import static org.apache.commons.lang3.Validate.isTrue;

public class EstudanteId {
    private final int id;

    public EstudanteId(int id) {
        isTrue(id > 0, "O id do estudante deve ser positivo");
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EstudanteId)) return false;
        return id == ((EstudanteId) o).id;
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }

    @Override
    public String toString() { return "EstudanteId{" + id + "}"; }
}
