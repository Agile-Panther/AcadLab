package school.cesar.acadlab.dominio.integralizacao.checklist;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class ItemChecklist {
    private final TipoItemChecklist tipo;
    private final String descricao;
    private final boolean cumprido;

    public ItemChecklist(TipoItemChecklist tipo, String descricao, boolean cumprido) {
        notNull(tipo, "O tipo do item não pode ser nulo");
        notBlank(descricao, "A descrição do item não pode estar em branco");
        this.tipo = tipo;
        this.descricao = descricao;
        this.cumprido = cumprido;
    }

    public TipoItemChecklist getTipo() { return tipo; }
    public String getDescricao() { return descricao; }
    public boolean isCumprido() { return cumprido; }
}
