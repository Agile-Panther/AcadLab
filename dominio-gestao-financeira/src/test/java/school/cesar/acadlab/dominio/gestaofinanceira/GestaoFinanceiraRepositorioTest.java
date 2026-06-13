package school.cesar.acadlab.dominio.gestaofinanceira;

import static org.apache.commons.lang3.Validate.notNull;
import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.*;
import java.util.*;

public class GestaoFinanceiraRepositorioTest implements CobrancaRepositorio {
    private int seq = 1;
    private final Map<CobrancaId, Cobranca> store = new HashMap<>();

    @Override
    public CobrancaId proximoId() { return new CobrancaId(seq++); }

    @Override
    public void salvar(Cobranca cobranca) {
        notNull(cobranca, "cobranca não pode ser nula");
        store.put(cobranca.getId(), cobranca);
    }

    @Override
    public Cobranca obter(CobrancaId id) {
        notNull(id, "id não pode ser nulo");
        return Optional.ofNullable(store.get(id)).orElseThrow();
    }

    @Override
    public List<Cobranca> pesquisarPorContrato(ContratoId contratoId) {
        return store.values().stream()
                .filter(c -> c.getContratoId().equals(contratoId))
                .toList();
    }
}
