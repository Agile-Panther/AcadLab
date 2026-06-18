package school.cesar.acadlab.dominio.permanenciaacademica;

import java.util.List;
import java.util.Optional;

public interface InscricaoRepositorio {
    InscricaoId proximoInscricaoId();
    void salvar(Inscricao inscricao);
    Inscricao obter(InscricaoId id);
    Optional<Inscricao> buscarPorEstudanteEEdital(EstudantePermanenciaId estudanteId, EditalId editalId);
    List<Inscricao> buscarPorEdital(EditalId editalId);
    List<Inscricao> buscarDeferidosPorEdital(EditalId editalId);
}
