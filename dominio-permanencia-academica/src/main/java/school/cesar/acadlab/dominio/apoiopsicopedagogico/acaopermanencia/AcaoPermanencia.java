package school.cesar.acadlab.dominio.apoiopsicopedagogico.acaopermanencia;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.CoordenadorId;

public class AcaoPermanencia {
    private final AcaoPermanenciaId id;
    private final CoordenadorId coordenadorId;
    private final String descricao;
    private final String indicadoresAgregados;
    private final LocalDate data;

    public AcaoPermanencia(AcaoPermanenciaId id, CoordenadorId coordenadorId,
                           String descricao, String indicadoresAgregados) {
        notNull(id, "O id não pode ser nulo");
        notNull(coordenadorId, "O coordenador não pode ser nulo");
        notNull(descricao, "A descrição não pode ser nula");
        notBlank(descricao, "A descrição não pode estar em branco");
        notNull(indicadoresAgregados, "Os indicadores agregados não podem ser nulos");
        notBlank(indicadoresAgregados, "Os indicadores agregados não podem estar em branco");
        this.id = id;
        this.coordenadorId = coordenadorId;
        this.descricao = descricao;
        this.indicadoresAgregados = indicadoresAgregados;
        this.data = LocalDate.now();
    }

    public AcaoPermanenciaId getId() { return id; }
    public CoordenadorId getCoordenadorId() { return coordenadorId; }
    public String getDescricao() { return descricao; }
    public String getIndicadoresAgregados() { return indicadoresAgregados; }
    public LocalDate getData() { return data; }
}
