package school.cesar.acadlab.aplicacao.atividadescomplementares;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;

public class AtividadeComplementarServicoAplicacao {
    private final AtividadeComplementarRepositorioAplicacao repositorio;

    public AtividadeComplementarServicoAplicacao(AtividadeComplementarRepositorioAplicacao repositorio) {
        notNull(repositorio, "repositório obrigatório");
        this.repositorio = repositorio;
    }

    public List<AtividadeComplementarResumo> pesquisarPorEstudante(int estudanteId) {
        return repositorio.pesquisarPorEstudante(estudanteId);
    }

    public List<AtividadeComplementarResumo> pesquisarPorStatus(String status) {
        return repositorio.pesquisarPorStatus(status);
    }
}
