package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import school.cesar.acadlab.dominio.gestaofinanceira.CobrancaId;
import school.cesar.acadlab.dominio.gestaofinanceira.ContratoId;
import java.util.List;

public interface CobrancaRepositorio {
    CobrancaId proximoId();
    void salvar(Cobranca cobranca);
    Cobranca obter(CobrancaId id);
    List<Cobranca> pesquisarPorContrato(ContratoId contratoId);
}
