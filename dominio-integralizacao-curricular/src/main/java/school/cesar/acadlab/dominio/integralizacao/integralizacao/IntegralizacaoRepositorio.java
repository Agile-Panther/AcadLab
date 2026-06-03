package school.cesar.acadlab.dominio.integralizacao.integralizacao;

import java.util.Optional;
import school.cesar.acadlab.dominio.integralizacao.EstudanteId;

public interface IntegralizacaoRepositorio {
    IntegralizacaoId proximoId();
    void salvar(IntegralizacaoCurricular integralizacao);
    IntegralizacaoCurricular obter(IntegralizacaoId id);
    Optional<IntegralizacaoCurricular> pesquisarPorEstudante(EstudanteId estudanteId);
}
