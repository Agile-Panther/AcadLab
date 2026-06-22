package school.cesar.acadlab.aplicacao.estagios;

import java.util.List;
import java.util.Optional;

public interface OportunidadeRepositorioAplicacao {
    Optional<OportunidadeResumo> buscarPorId(int id);
    List<OportunidadeResumo> listarAbertas();
    void excluir(int id);
}
