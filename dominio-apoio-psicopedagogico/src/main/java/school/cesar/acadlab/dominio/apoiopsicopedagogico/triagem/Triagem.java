package school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;

public class Triagem {
    private final PrioridadeTriagem prioridade;
    private final String observacoes;
    private final PsicopedagogoId responsavelId;
    private final LocalDate data;

    public Triagem(PrioridadeTriagem prioridade, String observacoes, PsicopedagogoId responsavelId, LocalDate data) {
        notNull(prioridade, "A prioridade não pode ser nula");
        notNull(observacoes, "As observações não podem ser nulas");
        notBlank(observacoes, "As observações não podem estar em branco");
        notNull(responsavelId, "O responsável não pode ser nulo");
        notNull(data, "A data não pode ser nula");

        this.prioridade = prioridade;
        this.observacoes = observacoes;
        this.responsavelId = responsavelId;
        this.data = data;
    }

    public PrioridadeTriagem getPrioridade() { return prioridade; }
    public String getObservacoes() { return observacoes; }
    public PsicopedagogoId getResponsavelId() { return responsavelId; }
    public LocalDate getData() { return data; }
}
