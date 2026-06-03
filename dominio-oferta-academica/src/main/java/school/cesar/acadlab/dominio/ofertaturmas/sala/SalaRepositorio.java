package school.cesar.acadlab.dominio.ofertaturmas.sala;

import java.util.List;

public interface SalaRepositorio {
    SalaId proximoId();
    void salvar(Sala sala);
    Sala obter(SalaId id);
    List<Sala> pesquisarAtivas();
}
