package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import java.util.List;

public interface BolsaRepositorio {
    BolsaId proximoId();
    void salvar(Bolsa bolsa);
    Bolsa obter(BolsaId id);
    List<Bolsa> listar();
}
