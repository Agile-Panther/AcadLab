package school.cesar.acadlab.dominio.matricula.matricula;

import static org.apache.commons.lang3.Validate.isTrue;

public class CoordenadorId {
    private final int id;

    public CoordenadorId(int id) {
        isTrue(id > 0, "O id do coordenador deve ser positivo");
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CoordenadorId)) return false;
        return id == ((CoordenadorId) o).id;
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }

    @Override
    public String toString() { return "CoordenadorId{" + id + "}"; }
}
