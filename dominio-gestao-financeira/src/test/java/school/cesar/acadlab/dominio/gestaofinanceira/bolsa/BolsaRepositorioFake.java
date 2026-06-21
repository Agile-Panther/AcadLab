package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import java.util.*;

public class BolsaRepositorioFake implements BolsaRepositorio {
    private int seq = 1;
    private final Map<BolsaId, Bolsa> store = new HashMap<>();

    @Override public BolsaId proximoId() { return new BolsaId(seq++); }
    @Override public void salvar(Bolsa bolsa) { store.put(bolsa.getId(), bolsa); }
    @Override public Bolsa obter(BolsaId id) { return Optional.ofNullable(store.get(id)).orElseThrow(); }
    @Override public java.util.List<Bolsa> listar() { return new ArrayList<>(store.values()); }
}
