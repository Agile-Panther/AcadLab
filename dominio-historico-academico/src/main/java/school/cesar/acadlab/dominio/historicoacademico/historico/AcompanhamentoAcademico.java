package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.notBlank;
import java.time.LocalDate;

public class AcompanhamentoAcademico {
    private final AcompanhamentoId id;
    private final String observacao;
    private final LocalDate data;

    public AcompanhamentoAcademico(AcompanhamentoId id, String observacao, LocalDate data) {
        notNull(id, "O id não pode ser nulo");
        notBlank(observacao, "A observação não pode ser vazia");
        notNull(data, "A data não pode ser nula");
        this.id = id;
        this.observacao = observacao;
        this.data = data;
    }

    public AcompanhamentoId getId() { return id; }
    public String getObservacao() { return observacao; }
    public LocalDate getData() { return data; }
}
