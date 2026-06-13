package school.cesar.acadlab.dominio.atividadescomplementares.atividade;

import school.cesar.acadlab.dominio.atividadescomplementares.AtividadeComplementarId;
import school.cesar.acadlab.dominio.atividadescomplementares.EstudanteId;
import java.util.List;

public interface AtividadeComplementarRepositorio {
    AtividadeComplementarId proximoId();
    void salvar(AtividadeComplementar atividade);
    AtividadeComplementar obter(AtividadeComplementarId id);
    List<AtividadeComplementar> pesquisarPorEstudante(EstudanteId estudanteId);
}
