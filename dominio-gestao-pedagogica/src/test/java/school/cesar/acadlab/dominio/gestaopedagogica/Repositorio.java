package school.cesar.acadlab.dominio.gestaopedagogica;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.AvaliacaoId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurma;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurmaId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurmaRepositorio;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.RegistroAulaId;

public class Repositorio implements DiarioTurmaRepositorio {

    private int proximoDiarioSeq = 1;
    private int proximaAulaSeq = 1;
    private int proximaAvaliacaoSeq = 1;
    private final Map<DiarioTurmaId, DiarioTurma> diarios = new HashMap<>();

    @Override
    public DiarioTurmaId proximoId() { return new DiarioTurmaId(proximoDiarioSeq++); }

    @Override
    public RegistroAulaId proximoAulaId() { return new RegistroAulaId(proximaAulaSeq++); }

    @Override
    public AvaliacaoId proximaAvaliacaoId() { return new AvaliacaoId(proximaAvaliacaoSeq++); }

    @Override
    public void salvar(DiarioTurma diario) {
        notNull(diario, "O diário não pode ser nulo");
        diarios.put(diario.getId(), diario);
    }

    @Override
    public DiarioTurma obter(DiarioTurmaId id) {
        notNull(id, "O id do diário não pode ser nulo");
        return Optional.ofNullable(diarios.get(id))
                .orElseThrow(() -> new IllegalArgumentException("Diário não encontrado: " + id));
    }
}
