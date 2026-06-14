package school.cesar.acadlab.aplicacao.mobilidadeacademica;

import java.util.List;
import java.util.Optional;

public interface MobilidadeAcademicaRepositorioAplicacao {
    List<MobilidadeAcademicaResumo> buscarPorEstudante(int estudanteId);
    Optional<MobilidadeAcademicaResumo> buscarPorId(int id);
}
