package school.cesar.acadlab.aplicacao.apoiopsicopedagogico;

import java.util.List;
import java.util.Optional;

public interface ApoioPsicopedagogicoRepositorioAplicacao {
    Optional<CasoResumo> buscarCasoPorId(int id);
    List<CasoResumo> buscarCasosPorResponsavel(int responsavelId);
    Optional<CasoResumo> buscarCasoAtivoPorEstudante(int estudanteId);
}
