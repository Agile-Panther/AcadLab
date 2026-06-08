package school.cesar.acadlab.dominio.integralizacao;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.*;
import school.cesar.acadlab.dominio.integralizacao.colacao.ColacaoDeGrau;
import school.cesar.acadlab.dominio.integralizacao.colacao.ColacaoId;
import school.cesar.acadlab.dominio.integralizacao.colacao.ColacaoRepositorio;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoCurricular;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoId;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoRepositorio;

public class Repositorio implements IntegralizacaoRepositorio, ColacaoRepositorio {

    /*-----------------------------------------------------------------------*/
    private int proximaIntegralizacaoIdSeq = 1;
    private final Map<IntegralizacaoId, IntegralizacaoCurricular> integralizacoes = new HashMap<>();

    @Override
    public IntegralizacaoId proximoId() { return new IntegralizacaoId(proximaIntegralizacaoIdSeq++); }

    @Override
    public void salvar(IntegralizacaoCurricular integralizacao) {
        notNull(integralizacao, "A integralização não pode ser nula");
        integralizacoes.put(integralizacao.getId(), integralizacao);
    }

    @Override
    public IntegralizacaoCurricular obter(IntegralizacaoId id) {
        notNull(id, "O id não pode ser nulo");
        return Optional.ofNullable(integralizacoes.get(id)).get();
    }

    @Override
    public Optional<IntegralizacaoCurricular> pesquisarPorEstudante(EstudanteId estudanteId) {
        return integralizacoes.values().stream()
                .filter(i -> i.getEstudanteId().equals(estudanteId))
                .findFirst();
    }
    /*-----------------------------------------------------------------------*/

    /*-----------------------------------------------------------------------*/
    private int proximaColacaoIdSeq = 1;
    private final Map<ColacaoId, ColacaoDeGrau> colacoes = new HashMap<>();

    @Override
    public ColacaoId proximoId() { return new ColacaoId(proximaColacaoIdSeq++); }

    @Override
    public void salvar(ColacaoDeGrau colacao) {
        notNull(colacao, "A colação não pode ser nula");
        colacoes.put(colacao.getId(), colacao);
    }

    @Override
    public ColacaoDeGrau obter(ColacaoId id) {
        notNull(id, "O id não pode ser nulo");
        return Optional.ofNullable(colacoes.get(id)).get();
    }

    @Override
    public Optional<ColacaoDeGrau> pesquisarPorEstudante(EstudanteId estudanteId) {
        return colacoes.values().stream()
                .filter(c -> c.getEstudanteId().equals(estudanteId))
                .findFirst();
    }
    /*-----------------------------------------------------------------------*/
}
