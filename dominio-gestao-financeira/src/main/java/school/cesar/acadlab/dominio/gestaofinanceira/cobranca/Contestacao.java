package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import static org.apache.commons.lang3.Validate.notNull;
import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
import school.cesar.acadlab.dominio.gestaofinanceira.StatusContestacao;
import java.time.LocalDate;

public class Contestacao {
    private final EstudanteId requerente;
    private final String justificativa;
    private final LocalDate dataContestacao;
    private StatusContestacao status;
    private String parecer;

    public Contestacao(EstudanteId requerente, String justificativa, LocalDate dataContestacao) {
        notNull(requerente, "requerente obrigatório");
        notNull(justificativa, "justificativa obrigatória");
        notNull(dataContestacao, "dataContestacao obrigatória");
        this.requerente = requerente;
        this.justificativa = justificativa;
        this.dataContestacao = dataContestacao;
        this.status = StatusContestacao.PENDENTE;
    }

    public void resolver(String parecer) {
        notNull(parecer, "parecer obrigatório");
        if (status != StatusContestacao.PENDENTE)
            throw new IllegalStateException("contestação já foi resolvida");
        this.status = StatusContestacao.RESOLVIDA;
        this.parecer = parecer;
    }

    public EstudanteId getRequerente() { return requerente; }
    public String getJustificativa() { return justificativa; }
    public LocalDate getDataContestacao() { return dataContestacao; }
    public StatusContestacao getStatus() { return status; }
    public String getParecer() { return parecer; }
}
