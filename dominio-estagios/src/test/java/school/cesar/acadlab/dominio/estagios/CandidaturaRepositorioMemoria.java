package school.cesar.acadlab.dominio.estagios;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import school.cesar.acadlab.dominio.estagios.candidatura.Candidatura;
import school.cesar.acadlab.dominio.estagios.candidatura.CandidaturaId;
import school.cesar.acadlab.dominio.estagios.candidatura.CandidaturaRepositorio;

public class CandidaturaRepositorioMemoria implements CandidaturaRepositorio {

    private int proximoIdSeq = 1;
    private final Map<CandidaturaId, Candidatura> candidaturas = new HashMap<>();

    @Override
    public CandidaturaId proximaCandidaturaId() {
        return new CandidaturaId(proximoIdSeq++);
    }

    @Override
    public void salvar(Candidatura candidatura) {
        candidaturas.put(candidatura.getId(), candidatura);
    }

    @Override
    public Optional<Candidatura> buscarPorId(CandidaturaId id) {
        return Optional.ofNullable(candidaturas.get(id));
    }
}
