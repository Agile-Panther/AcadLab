package school.cesar.acadlab.aplicacao.permanenciaacademica;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import java.util.Optional;

public class PermanenciaAcademicaServicoAplicacao {
    private final PermanenciaAcademicaRepositorioAplicacao repositorio;

    public PermanenciaAcademicaServicoAplicacao(PermanenciaAcademicaRepositorioAplicacao repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public List<EditalResumo> buscarTodosEditais() {
        return repositorio.buscarTodosEditais();
    }

    public List<EditalResumo> buscarEditaisPorPrograma(String programa) {
        return repositorio.buscarEditaisPorPrograma(programa);
    }

    public Optional<EditalResumo> buscarEditalPorId(int id) {
        return repositorio.buscarEditalPorId(id);
    }

    public List<InscricaoResumo> buscarTodasInscricoes() {
        return repositorio.buscarTodasInscricoes();
    }

    public List<InscricaoResumo> buscarInscricoesPorEdital(int editalId) {
        return repositorio.buscarInscricoesPorEdital(editalId);
    }

    public List<InscricaoResumo> buscarInscricoesPorEstudante(int estudanteId) {
        return repositorio.buscarInscricoesPorEstudante(estudanteId);
    }

    public List<BeneficioResumo> buscarTodosBeneficios() {
        return repositorio.buscarTodosBeneficios();
    }

    public List<BeneficioResumo> buscarBeneficiosPorEstudante(int estudanteId) {
        return repositorio.buscarBeneficiosPorEstudante(estudanteId);
    }

    public Optional<BeneficioResumo> buscarBeneficioPorId(int id) {
        return repositorio.buscarBeneficioPorId(id);
    }
}
