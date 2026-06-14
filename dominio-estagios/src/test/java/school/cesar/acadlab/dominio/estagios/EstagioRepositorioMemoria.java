package school.cesar.acadlab.dominio.estagios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import school.cesar.acadlab.dominio.estagios.estagio.Estagio;
import school.cesar.acadlab.dominio.estagios.estagio.EstagioId;
import school.cesar.acadlab.dominio.estagios.estagio.EstagioRepositorio;
import school.cesar.acadlab.dominio.estagios.oportunidade.EstudanteId;

public class EstagioRepositorioMemoria implements EstagioRepositorio {

    private int proximoIdSeq = 1;
    private final Map<EstagioId, Estagio> estagios = new HashMap<>();

    @Override
    public EstagioId proximoEstagioId() {
        return new EstagioId(proximoIdSeq++);
    }

    @Override
    public void salvar(Estagio estagio) {
        estagios.put(estagio.getId(), estagio);
    }

    @Override
    public Optional<Estagio> buscarPorId(EstagioId id) {
        return Optional.ofNullable(estagios.get(id));
    }

    @Override
    public List<Estagio> buscarPorEstudante(EstudanteId estudanteId) {
        var resultado = new ArrayList<Estagio>();
        for (var e : estagios.values()) {
            if (e.getEstudanteId().equals(estudanteId)) {
                resultado.add(e);
            }
        }
        return resultado;
    }
}
