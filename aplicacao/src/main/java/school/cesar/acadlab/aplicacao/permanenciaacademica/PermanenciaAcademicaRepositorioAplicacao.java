package school.cesar.acadlab.aplicacao.permanenciaacademica;

import java.util.List;
import java.util.Optional;

public interface PermanenciaAcademicaRepositorioAplicacao {
    List<EditalResumo> buscarEditaisPorPrograma(String programa);
    Optional<EditalResumo> buscarEditalPorId(int id);
    List<InscricaoResumo> buscarInscricoesPorEdital(int editalId);
    List<InscricaoResumo> buscarInscricoesPorEstudante(int estudanteId);
    List<BeneficioResumo> buscarBeneficiosPorEstudante(int estudanteId);
    Optional<BeneficioResumo> buscarBeneficioPorId(int id);
}
