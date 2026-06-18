package school.cesar.acadlab.aplicacao.apoiopsicopedagogico;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import java.util.Optional;

public class ApoioPsicopedagogicoServicoAplicacao {
    private final ApoioPsicopedagogicoRepositorioAplicacao repositorio;

    public ApoioPsicopedagogicoServicoAplicacao(ApoioPsicopedagogicoRepositorioAplicacao repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public Optional<CasoResumo> buscarCasoPorId(int id) {
        return repositorio.buscarCasoPorId(id);
    }

    public List<CasoResumo> buscarCasosPorResponsavel(int responsavelId) {
        return repositorio.buscarCasosPorResponsavel(responsavelId);
    }

    public Optional<CasoResumo> buscarCasoAtivoPorEstudante(int estudanteId) {
        return repositorio.buscarCasoAtivoPorEstudante(estudanteId);
    }
}
