package school.cesar.acadlab.dominio.apoiopsicopedagogico.caso;

import java.util.List;
import java.util.Optional;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;

public interface CasoRepositorio {
    CasoId proximoId();
    void salvar(Caso caso);
    Caso obter(CasoId id);
    Optional<Caso> pesquisarCasoAbertoPorEstudante(EstudanteId estudanteId);
    Optional<Caso> pesquisarUltimoCasoEncerradoPorEstudante(EstudanteId estudanteId);
    List<Caso> pesquisarPorResponsavel(PsicopedagogoId responsavelId);
}
