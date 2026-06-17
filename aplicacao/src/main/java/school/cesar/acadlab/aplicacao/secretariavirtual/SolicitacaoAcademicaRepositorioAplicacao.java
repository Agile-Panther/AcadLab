package school.cesar.acadlab.aplicacao.secretariavirtual;

import java.util.List;
import java.util.Optional;

public interface SolicitacaoAcademicaRepositorioAplicacao {
    List<SolicitacaoAcademicaResumo> buscarPorEstudante(int estudanteId);
    Optional<SolicitacaoAcademicaResumo> buscarPorId(int id);
    List<SolicitacaoAcademicaResumo> buscarPorStatus(String status);
}
