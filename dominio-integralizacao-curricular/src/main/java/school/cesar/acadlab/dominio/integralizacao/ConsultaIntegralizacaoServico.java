package school.cesar.acadlab.dominio.integralizacao;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.Optional;
import school.cesar.acadlab.dominio.integralizacao.colacao.ColacaoDeGrau;
import school.cesar.acadlab.dominio.integralizacao.colacao.ColacaoRepositorio;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoCurricular;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoId;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoRepositorio;

public class ConsultaIntegralizacaoServico {
    private final IntegralizacaoRepositorio integralizacaoRepositorio;
    private final ColacaoRepositorio colacaoRepositorio;

    public ConsultaIntegralizacaoServico(IntegralizacaoRepositorio integralizacaoRepositorio,
                                          ColacaoRepositorio colacaoRepositorio) {
        notNull(integralizacaoRepositorio, "O repositório de integralizações não pode ser nulo");
        notNull(colacaoRepositorio, "O repositório de colações não pode ser nulo");
        this.integralizacaoRepositorio = integralizacaoRepositorio;
        this.colacaoRepositorio = colacaoRepositorio;
    }

    public IntegralizacaoCurricular buscar(IntegralizacaoId id) {
        notNull(id, "O id não pode ser nulo");
        return integralizacaoRepositorio.obter(id);
    }

    public Optional<IntegralizacaoCurricular> buscarPorEstudante(EstudanteId estudanteId) {
        notNull(estudanteId, "O id do estudante não pode ser nulo");
        return integralizacaoRepositorio.pesquisarPorEstudante(estudanteId);
    }

    public Optional<ColacaoDeGrau> buscarColacaoPorEstudante(EstudanteId estudanteId) {
        notNull(estudanteId, "O id do estudante não pode ser nulo");
        return colacaoRepositorio.pesquisarPorEstudante(estudanteId);
    }
}
