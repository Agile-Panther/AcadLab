package school.cesar.acadlab.aplicacao.ofertaturmas;
import java.util.List;
import java.util.Optional;
public interface SalaRepositorioAplicacao {
    Optional<SalaResumo> buscarPorId(int id);
    List<SalaResumo> listarAtivas();
}
