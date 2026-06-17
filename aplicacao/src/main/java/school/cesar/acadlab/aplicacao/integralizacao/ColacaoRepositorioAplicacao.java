package school.cesar.acadlab.aplicacao.integralizacao;

import java.util.Optional;

public interface ColacaoRepositorioAplicacao {
    Optional<ColacaoResumo> buscarPorEstudante(int estudanteId);
    Optional<ColacaoResumo> buscarPorId(int id);
}
