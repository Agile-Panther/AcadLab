package school.cesar.acadlab.dominio.permanenciaacademica;

import java.util.List;
import java.util.Optional;

public interface BeneficioRepositorio {
    BeneficioId proximoBeneficioId();
    void salvar(Beneficio beneficio);
    Beneficio obter(BeneficioId id);
    Optional<Beneficio> buscarPorInscricao(InscricaoId inscricaoId);
    List<Beneficio> buscarPorEstudante(EstudantePermanenciaId estudanteId);
}
