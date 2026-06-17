package school.cesar.acadlab.aplicacao.ofertaturmas;
import java.util.List;
import java.util.Optional;
public interface ProfessorRepositorioAplicacao {
    Optional<ProfessorResumo> buscarPorId(int id);
    List<ProfessorResumo> listarAtivos();
}
