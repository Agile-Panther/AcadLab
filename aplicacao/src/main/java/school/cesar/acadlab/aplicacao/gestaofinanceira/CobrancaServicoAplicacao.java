package school.cesar.acadlab.aplicacao.gestaofinanceira;

import java.util.List;

public class CobrancaServicoAplicacao {
    private final CobrancaRepositorioAplicacao repositorio;

    public CobrancaServicoAplicacao(CobrancaRepositorioAplicacao repositorio) {
        this.repositorio = repositorio;
    }

    public List<CobrancaResumo> pesquisarPorContrato(int contratoId) {
        return repositorio.pesquisarPorContrato(contratoId);
    }
}
