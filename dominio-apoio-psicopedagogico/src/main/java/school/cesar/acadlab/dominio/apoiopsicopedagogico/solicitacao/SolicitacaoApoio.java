package school.cesar.acadlab.dominio.apoiopsicopedagogico.solicitacao;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;

public class SolicitacaoApoio {
    private final SolicitacaoApoioId id;
    private final EstudanteId estudanteId;
    private final String descricao;
    private final LocalDate dataSolicitacao;

    public SolicitacaoApoio(SolicitacaoApoioId id, EstudanteId estudanteId, String descricao) {
        notNull(id, "O id não pode ser nulo");
        notNull(estudanteId, "O estudante não pode ser nulo");
        notNull(descricao, "A descrição não pode ser nula");
        notBlank(descricao, "A descrição não pode estar em branco");
        this.id = id;
        this.estudanteId = estudanteId;
        this.descricao = descricao;
        this.dataSolicitacao = LocalDate.now();
    }

    public SolicitacaoApoioId getId() { return id; }
    public EstudanteId getEstudanteId() { return estudanteId; }
    public String getDescricao() { return descricao; }
    public LocalDate getDataSolicitacao() { return dataSolicitacao; }
}
