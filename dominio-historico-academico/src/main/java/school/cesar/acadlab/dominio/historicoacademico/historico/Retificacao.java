package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.notBlank;
import java.time.LocalDate;

public class Retificacao {
    private final RetificacaoId id;
    private final RegistroDisciplinaId registroId;
    private final SituacaoAcademica situacaoAnterior;
    private final SituacaoAcademica novaSituacao;
    private final SecretariaId responsavel;
    private final String justificativa;
    private final LocalDate data;

    public Retificacao(RetificacaoId id, RegistroDisciplinaId registroId,
                        SituacaoAcademica situacaoAnterior, SituacaoAcademica novaSituacao,
                        SecretariaId responsavel, String justificativa, LocalDate data) {
        notNull(id, "O id não pode ser nulo");
        notNull(registroId, "O registro não pode ser nulo");
        notNull(situacaoAnterior, "A situação anterior não pode ser nula");
        notNull(novaSituacao, "A nova situação não pode ser nula");
        notNull(responsavel, "O responsável não pode ser nulo");
        notBlank(justificativa, "A justificativa não pode ser vazia");
        notNull(data, "A data não pode ser nula");
        this.id = id;
        this.registroId = registroId;
        this.situacaoAnterior = situacaoAnterior;
        this.novaSituacao = novaSituacao;
        this.responsavel = responsavel;
        this.justificativa = justificativa;
        this.data = data;
    }

    public RetificacaoId getId() { return id; }
    public RegistroDisciplinaId getRegistroId() { return registroId; }
    public SituacaoAcademica getSituacaoAnterior() { return situacaoAnterior; }
    public SituacaoAcademica getNovaSituacao() { return novaSituacao; }
    public SecretariaId getResponsavel() { return responsavel; }
    public String getJustificativa() { return justificativa; }
    public LocalDate getData() { return data; }
}
