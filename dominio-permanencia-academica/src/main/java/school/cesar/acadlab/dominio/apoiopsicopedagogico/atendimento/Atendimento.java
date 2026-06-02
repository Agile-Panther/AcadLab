package school.cesar.acadlab.dominio.apoiopsicopedagogico.atendimento;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;

public class Atendimento {
    private final String observacoes;
    private final String encaminhamento;
    private final boolean conclusaoFinal;
    private final LocalDate data;

    public Atendimento(String observacoes, String encaminhamento, boolean conclusaoFinal, LocalDate data) {
        notNull(observacoes, "As observações não podem ser nulas");
        notBlank(observacoes, "As observações não podem estar em branco");
        notNull(data, "A data não pode ser nula");

        this.observacoes = observacoes;
        this.encaminhamento = encaminhamento;
        this.conclusaoFinal = conclusaoFinal;
        this.data = data;
    }

    public String getObservacoes() { return observacoes; }
    public String getEncaminhamento() { return encaminhamento; }
    public boolean isConclusaoFinal() { return conclusaoFinal; }
    public LocalDate getData() { return data; }
}
