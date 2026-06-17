package school.cesar.acadlab.aplicacao.ofertaturmas;
import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import java.util.Optional;
public class TurmaServicoAplicacao {
    private final TurmaRepositorioAplicacao repositorio;
    public TurmaServicoAplicacao(TurmaRepositorioAplicacao repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }
    public Optional<TurmaResumo> buscarPorId(int id) { return repositorio.buscarPorId(id); }
    public List<TurmaResumo> listarPorPeriodo(int periodoLetivoId) { return repositorio.listarPorPeriodo(periodoLetivoId); }
}
