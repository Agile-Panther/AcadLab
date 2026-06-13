package school.cesar.acadlab.dominio.atividadescomplementares;

import static org.apache.commons.lang3.Validate.notNull;
import school.cesar.acadlab.dominio.atividadescomplementares.atividade.*;
import java.util.*;

public class AtividadesComplementaresRepositorioTest implements AtividadeComplementarRepositorio {
    private int seq = 1;
    private final Map<AtividadeComplementarId, AtividadeComplementar> store = new HashMap<>();

    @Override
    public AtividadeComplementarId proximoId() { return new AtividadeComplementarId(seq++); }

    @Override
    public void salvar(AtividadeComplementar atividade) {
        notNull(atividade, "atividade não pode ser nula");
        store.put(atividade.getId(), atividade);
    }

    @Override
    public AtividadeComplementar obter(AtividadeComplementarId id) {
        notNull(id, "id não pode ser nulo");
        return Optional.ofNullable(store.get(id)).orElseThrow();
    }

    @Override
    public List<AtividadeComplementar> pesquisarPorEstudante(EstudanteId estudanteId) {
        return store.values().stream()
                .filter(a -> a.getEstudanteId().equals(estudanteId))
                .toList();
    }
}
