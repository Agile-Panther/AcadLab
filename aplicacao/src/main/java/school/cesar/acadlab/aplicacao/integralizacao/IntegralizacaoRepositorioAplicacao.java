package school.cesar.acadlab.aplicacao.integralizacao;

import java.util.List;
import java.util.Optional;

public interface IntegralizacaoRepositorioAplicacao {
    List<IntegralizacaoResumo> buscarPorEstudante(int estudanteId);
    Optional<IntegralizacaoResumo> buscarPorId(int id);
}
