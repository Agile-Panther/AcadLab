package school.cesar.acadlab.dominio.integralizacao;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.*;
import school.cesar.acadlab.dominio.integralizacao.colacao.ColacaoDeGrau;
import school.cesar.acadlab.dominio.integralizacao.colacao.ColacaoId;
import school.cesar.acadlab.dominio.integralizacao.colacao.ColacaoRepositorio;

public class ColacaoRepositorioTest implements ColacaoRepositorio {
    private int proximoSeq = 1;
    private final Map<ColacaoId, ColacaoDeGrau> colacoes = new HashMap<>();

    @Override
    public ColacaoId proximoId() { return new ColacaoId(proximoSeq++); }

    @Override
    public void salvar(ColacaoDeGrau colacao) {
        notNull(colacao, "A colação não pode ser nula");
        colacoes.put(colacao.getId(), colacao);
    }

    @Override
    public ColacaoDeGrau obter(ColacaoId id) {
        notNull(id, "O id não pode ser nulo");
        return Optional.ofNullable(colacoes.get(id)).orElseThrow();
    }

    @Override
    public Optional<ColacaoDeGrau> pesquisarPorEstudante(EstudanteId estudanteId) {
        return colacoes.values().stream()
                .filter(c -> c.getEstudanteId().equals(estudanteId))
                .findFirst();
    }
}
