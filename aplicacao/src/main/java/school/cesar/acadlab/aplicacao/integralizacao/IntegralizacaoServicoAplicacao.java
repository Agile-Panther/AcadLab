package school.cesar.acadlab.aplicacao.integralizacao;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import java.util.Optional;

public class IntegralizacaoServicoAplicacao {

    private final IntegralizacaoRepositorioAplicacao integralizacaoRepositorio;
    private final ColacaoRepositorioAplicacao colacaoRepositorio;

    public IntegralizacaoServicoAplicacao(IntegralizacaoRepositorioAplicacao integralizacaoRepositorio,
                                          ColacaoRepositorioAplicacao colacaoRepositorio) {
        notNull(integralizacaoRepositorio, "repositório de integralizações obrigatório");
        notNull(colacaoRepositorio, "repositório de colações obrigatório");
        this.integralizacaoRepositorio = integralizacaoRepositorio;
        this.colacaoRepositorio = colacaoRepositorio;
    }

    public List<IntegralizacaoResumo> buscarIntegralizacoesPorEstudante(int estudanteId) {
        return integralizacaoRepositorio.buscarPorEstudante(estudanteId);
    }

    public Optional<IntegralizacaoResumo> buscarIntegralizacaoPorId(int id) {
        return integralizacaoRepositorio.buscarPorId(id);
    }

    public Optional<ColacaoResumo> buscarColacaoPorEstudante(int estudanteId) {
        return colacaoRepositorio.buscarPorEstudante(estudanteId);
    }

    public Optional<ColacaoResumo> buscarColacaoPorId(int id) {
        return colacaoRepositorio.buscarPorId(id);
    }
}
