package school.cesar.acadlab.dominio.estagios.oportunidade;

import java.util.Optional;

public interface OportunidadeRepositorio {
    OportunidadeId proximaOportunidadeId();
    void salvar(Oportunidade oportunidade);
    Optional<Oportunidade> buscarPorId(OportunidadeId id);
}
