package school.cesar.acadlab.dominio.ofertaturmas.turma.decorator;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TurmaComListaEspera extends TurmaDecorador {
    private final List<EstudanteId> listaEspera = new ArrayList<>();

    public TurmaComListaEspera(TurmaOferecida turma) {
        super(turma);
    }

    /** Reidrata o decorator a partir da lista de espera já persistida na turma. */
    public TurmaComListaEspera(TurmaOferecida turma, List<EstudanteId> existentes) {
        super(turma);
        if (existentes != null) {
            listaEspera.addAll(existentes);
        }
    }

    public void entrarListaEspera(EstudanteId estudanteId) {
        notNull(estudanteId, "O estudante não pode ser nulo");
        if (listaEspera.contains(estudanteId))
            throw new IllegalStateException("estudante já está na lista de espera desta turma");
        listaEspera.add(estudanteId);
    }

    public void sairListaEspera(EstudanteId estudanteId) {
        notNull(estudanteId, "O estudante não pode ser nulo");
        if (!listaEspera.remove(estudanteId))
            throw new IllegalStateException("Estudante não está na lista de espera");
    }

    public List<EstudanteId> getListaEspera() {
        return Collections.unmodifiableList(listaEspera);
    }

    public Optional<EstudanteId> proximoDaEspera() {
        return listaEspera.isEmpty() ? Optional.empty() : Optional.of(listaEspera.get(0));
    }
}
