package school.cesar.acadlab.aplicacao.atividadescomplementares;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;

public class CategoriaHorasServicoAplicacao {
    private final CategoriaHorasRepositorioAplicacao repositorio;

    public CategoriaHorasServicoAplicacao(CategoriaHorasRepositorioAplicacao repositorio) {
        notNull(repositorio, "repositório obrigatório");
        this.repositorio = repositorio;
    }

    public List<CategoriaHorasResumo> listar() {
        return repositorio.listar();
    }
}
