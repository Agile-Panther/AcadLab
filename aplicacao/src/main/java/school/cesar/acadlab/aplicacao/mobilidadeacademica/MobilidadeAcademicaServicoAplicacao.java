package school.cesar.acadlab.aplicacao.mobilidadeacademica;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import java.util.Optional;

public class MobilidadeAcademicaServicoAplicacao {

    private final MobilidadeAcademicaRepositorioAplicacao repositorio;

    public MobilidadeAcademicaServicoAplicacao(MobilidadeAcademicaRepositorioAplicacao repositorio) {
        notNull(repositorio, "repositório obrigatório");
        this.repositorio = repositorio;
    }

    public List<MobilidadeAcademicaResumo> buscarPorEstudante(int estudanteId) {
        return repositorio.buscarPorEstudante(estudanteId);
    }

    public Optional<MobilidadeAcademicaResumo> buscarPorId(int id) {
        return repositorio.buscarPorId(id);
    }

    public List<MobilidadeAcademicaResumo> listarTodos() {
        return repositorio.listarTodos();
    }
}
