package school.cesar.acadlab.dominio.permanenciaacademica;

import java.util.List;
import java.util.Optional;

public interface EditalRepositorio {
    EditalId proximoEditalId();
    void salvar(Edital edital);
    Edital obter(EditalId id);
    Optional<Edital> buscarPorId(EditalId id);
    boolean existeEditalAbertoParaPrograma(String programa);
    List<Edital> buscarPorPrograma(String programa);
}
