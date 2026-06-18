package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.notBlank;
import java.time.LocalDate;

public class EntradaAuditoria {
    private final SituacaoDiscente situacaoAnterior;
    private final SituacaoDiscente novaSituacao;
    private final SecretariaId responsavel;
    private final String justificativa;
    private final LocalDate data;

    public EntradaAuditoria(SituacaoDiscente situacaoAnterior, SituacaoDiscente novaSituacao,
                             SecretariaId responsavel, String justificativa, LocalDate data) {
        notNull(situacaoAnterior, "A situação anterior não pode ser nula");
        notNull(novaSituacao, "A nova situação não pode ser nula");
        notNull(responsavel, "O responsável não pode ser nulo");
        notBlank(justificativa, "A justificativa não pode ser vazia");
        notNull(data, "A data não pode ser nula");
        this.situacaoAnterior = situacaoAnterior;
        this.novaSituacao = novaSituacao;
        this.responsavel = responsavel;
        this.justificativa = justificativa;
        this.data = data;
    }

    public SituacaoDiscente getSituacaoAnterior() { return situacaoAnterior; }
    public SituacaoDiscente getNovaSituacao() { return novaSituacao; }
    public SecretariaId getResponsavel() { return responsavel; }
    public String getJustificativa() { return justificativa; }
    public LocalDate getData() { return data; }
}
