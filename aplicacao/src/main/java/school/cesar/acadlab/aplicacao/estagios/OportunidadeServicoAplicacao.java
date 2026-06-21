package school.cesar.acadlab.aplicacao.estagios;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import java.util.Optional;

public class OportunidadeServicoAplicacao {

    private final OportunidadeRepositorioAplicacao repositorio;

    public OportunidadeServicoAplicacao(OportunidadeRepositorioAplicacao repositorio) {
        notNull(repositorio, "Repositório obrigatório");
        this.repositorio = repositorio;
    }

    public Optional<OportunidadeResumo> buscarPorId(int id) {
        return repositorio.buscarPorId(id);
    }

    public List<OportunidadeResumo> listarAbertas() {
        return repositorio.listarAbertas();
    }

    public void excluir(int id) {
        repositorio.excluir(id);
    }
}
