package school.cesar.acadlab.dominio.integralizacao;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.*;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoCurricular;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoId;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoRepositorio;

public class IntegralizacaoRepositorioTest implements IntegralizacaoRepositorio {
    private int proximoSeq = 1;
    private final Map<IntegralizacaoId, IntegralizacaoCurricular> integralizacoes = new HashMap<>();

    @Override
    public IntegralizacaoId proximoId() { return new IntegralizacaoId(proximoSeq++); }

    @Override
    public void salvar(IntegralizacaoCurricular integralizacao) {
        notNull(integralizacao, "A integralização não pode ser nula");
        integralizacoes.put(integralizacao.getId(), integralizacao);
    }

    @Override
    public IntegralizacaoCurricular obter(IntegralizacaoId id) {
        notNull(id, "O id não pode ser nulo");
        return Optional.ofNullable(integralizacoes.get(id)).orElseThrow();
    }

    @Override
    public Optional<IntegralizacaoCurricular> pesquisarPorEstudante(EstudanteId estudanteId) {
        return integralizacoes.values().stream()
                .filter(i -> i.getEstudanteId().equals(estudanteId))
                .findFirst();
    }
}
