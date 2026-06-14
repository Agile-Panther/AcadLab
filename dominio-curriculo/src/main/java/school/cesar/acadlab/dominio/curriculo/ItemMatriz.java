package school.cesar.acadlab.dominio.curriculo;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;
import java.util.Objects;

public final class ItemMatriz {
    private final DisciplinaId disciplinaId;
    private final TipoDisciplina tipo;
    private final int cargaHoraria;
    private final int creditos;

    public ItemMatriz(DisciplinaId disciplinaId, TipoDisciplina tipo, int cargaHoraria, int creditos) {
        notNull(disciplinaId, "DisciplinaId não pode ser nulo");
        notNull(tipo, "TipoDisciplina não pode ser nulo");
        isTrue(cargaHoraria > 0, "Carga horária deve ser positiva");
        isTrue(creditos > 0, "Créditos devem ser positivos");
        this.disciplinaId = disciplinaId;
        this.tipo = tipo;
        this.cargaHoraria = cargaHoraria;
        this.creditos = creditos;
    }

    public DisciplinaId getDisciplinaId() { return disciplinaId; }
    public TipoDisciplina getTipo() { return tipo; }
    public int getCargaHoraria() { return cargaHoraria; }
    public int getCreditos() { return creditos; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ItemMatriz)) return false;
        ItemMatriz other = (ItemMatriz) obj;
        return Objects.equals(disciplinaId, other.disciplinaId);
    }

    @Override
    public int hashCode() { return Objects.hash(disciplinaId); }
}
