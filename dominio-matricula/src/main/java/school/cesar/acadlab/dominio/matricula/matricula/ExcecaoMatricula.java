package school.cesar.acadlab.dominio.matricula.matricula;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class ExcecaoMatricula {
    private final DisciplinaId disciplinaId;
    private final String motivo;
    private boolean deferida;
    private CoordenadorId coordenadorId;

    public ExcecaoMatricula(DisciplinaId disciplinaId, String motivo) {
        notNull(disciplinaId, "O id da disciplina não pode ser nulo");
        notBlank(motivo, "O motivo da exceção não pode ser vazio");
        this.disciplinaId = disciplinaId;
        this.motivo = motivo;
        this.deferida = false;
    }

    private ExcecaoMatricula(DisciplinaId disciplinaId, String motivo,
                              boolean deferida, CoordenadorId coordenadorId) {
        this.disciplinaId = disciplinaId;
        this.motivo = motivo;
        this.deferida = deferida;
        this.coordenadorId = coordenadorId;
    }

    public static ExcecaoMatricula reconstituir(DisciplinaId disciplinaId, String motivo,
                                                 boolean deferida, CoordenadorId coordenadorId) {
        return new ExcecaoMatricula(disciplinaId, motivo, deferida, coordenadorId);
    }

    public void deferir(CoordenadorId coordenadorId) {
        notNull(coordenadorId, "O coordenador não pode ser nulo");
        isTrue(!deferida, "A exceção já foi deferida");
        this.deferida = true;
        this.coordenadorId = coordenadorId;
    }

    public DisciplinaId getDisciplinaId() { return disciplinaId; }
    public String getMotivo() { return motivo; }
    public boolean isDeferida() { return deferida; }
    public CoordenadorId getCoordenadorId() { return coordenadorId; }
}
