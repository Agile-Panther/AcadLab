package school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade;

import java.util.List;
import java.util.Optional;

public interface MobilidadeAcademicaRepositorio {
    void salvar(MobilidadeAcademica mobilidade);
    Optional<MobilidadeAcademica> buscarPorId(MobilidadeAcademicaId id);
    List<MobilidadeAcademica> buscarPorEstudante(EstudanteId estudanteId);
    MobilidadeAcademicaId proximaMobilidadeId();
}
