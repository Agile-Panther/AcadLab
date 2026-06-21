package school.cesar.acadlab.aplicacao.gestaofinanceira;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;

public class BolsaServicoAplicacao {
    private final BolsaRepositorioAplicacao repositorio;

    public BolsaServicoAplicacao(BolsaRepositorioAplicacao repositorio) {
        notNull(repositorio, "repositório obrigatório");
        this.repositorio = repositorio;
    }

    public List<BolsaResumo> listar() { return repositorio.listarResumos(); }
}
