package school.cesar.acadlab.aplicacao.ofertaturmas;
import java.util.List;
import java.util.Optional;
public interface TurmaRepositorioAplicacao {
    Optional<TurmaResumo> buscarPorId(int id);
    List<TurmaResumo> listarPorPeriodo(int periodoLetivoId);
    List<TurmaResumo> listarComFiltros(Integer periodoLetivoId, Integer cursoId,
            Integer disciplinaId, Integer professorId, String status);
}
