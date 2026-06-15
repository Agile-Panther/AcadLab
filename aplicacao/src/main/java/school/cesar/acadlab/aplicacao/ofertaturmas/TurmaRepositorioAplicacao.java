package school.cesar.acadlab.aplicacao.ofertaturmas;
import java.util.List;
import java.util.Optional;
public interface TurmaRepositorioAplicacao {
    Optional<TurmaResumo> buscarPorId(int id);
    List<TurmaResumo> listarPorPeriodo(int periodoLetivoId);
}
