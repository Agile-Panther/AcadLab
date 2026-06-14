package school.cesar.acadlab.dominio.matricula.matricula;

import static org.apache.commons.lang3.Validate.isTrue;

public class PeriodoLetivoId {
    private final int id;

    public PeriodoLetivoId(int id) {
        isTrue(id > 0, "O id do período letivo deve ser positivo");
        this.id = id;
    }

    public int getId() { return id; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PeriodoLetivoId)) return false;
        return id == ((PeriodoLetivoId) o).id;
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }

    @Override
    public String toString() { return "PeriodoLetivoId{" + id + "}"; }
}
