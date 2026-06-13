package school.cesar.acadlab.dominio.ofertaturmas;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.*;
import school.cesar.acadlab.dominio.ofertaturmas.sala.Sala;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaId;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaRepositorio;

public class SalaRepositorioTest implements SalaRepositorio {
    private int proximoSeq = 1;
    private final Map<SalaId, Sala> salas = new HashMap<>();

    @Override
    public SalaId proximoId() { return new SalaId(proximoSeq++); }

    @Override
    public void salvar(Sala sala) {
        notNull(sala, "A sala não pode ser nula");
        salas.put(sala.getId(), sala);
    }

    @Override
    public Sala obter(SalaId id) {
        notNull(id, "O id da sala não pode ser nulo");
        return Optional.ofNullable(salas.get(id)).orElseThrow();
    }

    @Override
    public List<Sala> pesquisarAtivas() {
        var resultado = new ArrayList<Sala>();
        for (var s : salas.values())
            if (s.isAtiva()) resultado.add(s);
        return resultado;
    }
}
