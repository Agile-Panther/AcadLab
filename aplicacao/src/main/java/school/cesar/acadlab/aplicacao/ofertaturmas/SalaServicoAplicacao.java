package school.cesar.acadlab.aplicacao.ofertaturmas;
import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import java.util.Optional;
public class SalaServicoAplicacao {
    private final SalaRepositorioAplicacao repositorio;
    public SalaServicoAplicacao(SalaRepositorioAplicacao repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }
    public Optional<SalaResumo> buscarPorId(int id) { return repositorio.buscarPorId(id); }
    public List<SalaResumo> listarAtivas() { return repositorio.listarAtivas(); }
}
