package school.cesar.acadlab.dominio.apoiopsicopedagogico;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoRepositorio;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;

public class ConsultaServico {
    private final CasoRepositorio casoRepositorio;

    public ConsultaServico(CasoRepositorio casoRepositorio) {
        notNull(casoRepositorio, "O repositório de casos não pode ser nulo");
        this.casoRepositorio = casoRepositorio;
    }

    public List<Caso> listarCasosPorResponsavel(PsicopedagogoId responsavelId) {
        notNull(responsavelId, "O id do responsável não pode ser nulo");
        return casoRepositorio.pesquisarPorResponsavel(responsavelId);
    }
}
