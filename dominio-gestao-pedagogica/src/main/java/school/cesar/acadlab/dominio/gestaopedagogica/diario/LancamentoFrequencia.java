package school.cesar.acadlab.dominio.gestaopedagogica.diario;

import static org.apache.commons.lang3.Validate.notNull;

public class LancamentoFrequencia {
    private final RegistroAulaId aulaId;
    private final EstudanteId estudanteId;
    private final boolean presente;

    public LancamentoFrequencia(RegistroAulaId aulaId, EstudanteId estudanteId, boolean presente) {
        notNull(aulaId, "O id da aula não pode ser nulo");
        notNull(estudanteId, "O id do estudante não pode ser nulo");
        this.aulaId = aulaId;
        this.estudanteId = estudanteId;
        this.presente = presente;
    }

    public RegistroAulaId getAulaId() { return aulaId; }
    public EstudanteId getEstudanteId() { return estudanteId; }
    public boolean isPresente() { return presente; }
}
