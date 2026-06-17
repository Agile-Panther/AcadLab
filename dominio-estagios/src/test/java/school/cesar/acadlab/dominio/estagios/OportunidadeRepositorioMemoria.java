package school.cesar.acadlab.dominio.estagios;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import school.cesar.acadlab.dominio.estagios.oportunidade.Oportunidade;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeId;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeRepositorio;

public class OportunidadeRepositorioMemoria implements OportunidadeRepositorio {

    private int proximoIdSeq = 1;
    private final Map<OportunidadeId, Oportunidade> oportunidades = new HashMap<>();

    @Override
    public OportunidadeId proximaOportunidadeId() {
        return new OportunidadeId(proximoIdSeq++);
    }

    @Override
    public void salvar(Oportunidade oportunidade) {
        oportunidades.put(oportunidade.getId(), oportunidade);
    }

    @Override
    public Optional<Oportunidade> buscarPorId(OportunidadeId id) {
        return Optional.ofNullable(oportunidades.get(id));
    }
}
