package school.cesar.acadlab.aplicacao.atividadescomplementares;

import java.util.List;

public interface AtividadeComplementarRepositorioAplicacao {
    List<AtividadeComplementarResumo> pesquisarPorEstudante(int estudanteId);
    List<AtividadeComplementarResumo> pesquisarPorStatus(String status);
}
