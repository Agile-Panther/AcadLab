package school.cesar.acadlab.dominio.estagios.candidatura;

import java.util.Optional;

public interface CandidaturaRepositorio {
    CandidaturaId proximaCandidaturaId();
    void salvar(Candidatura candidatura);
    Optional<Candidatura> buscarPorId(CandidaturaId id);
}
