package school.cesar.acadlab.aplicacao.ofertaturmas;
import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import java.util.Optional;
public class ProfessorServicoAplicacao {
    private final ProfessorRepositorioAplicacao repositorio;
    public ProfessorServicoAplicacao(ProfessorRepositorioAplicacao repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }
    public Optional<ProfessorResumo> buscarPorId(int id) { return repositorio.buscarPorId(id); }
    public List<ProfessorResumo> listarAtivos() { return repositorio.listarAtivos(); }
}
