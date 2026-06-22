package school.cesar.acadlab.aplicacao.estagios;

import java.util.List;

public interface CandidaturaRepositorioAplicacao {
    List<CandidaturaResumo> listarTodas();
    List<CandidaturaResumo> buscarPorEstudante(int estudanteId);
}
