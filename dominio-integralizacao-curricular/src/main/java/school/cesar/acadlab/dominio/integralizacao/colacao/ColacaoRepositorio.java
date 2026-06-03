package school.cesar.acadlab.dominio.integralizacao.colacao;

import java.util.Optional;
import school.cesar.acadlab.dominio.integralizacao.EstudanteId;

public interface ColacaoRepositorio {
    ColacaoId proximoId();
    void salvar(ColacaoDeGrau colacao);
    ColacaoDeGrau obter(ColacaoId id);
    Optional<ColacaoDeGrau> pesquisarPorEstudante(EstudanteId estudanteId);
}
