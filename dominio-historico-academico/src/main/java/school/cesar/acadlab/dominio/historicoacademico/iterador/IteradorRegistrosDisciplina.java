package school.cesar.acadlab.dominio.historicoacademico.iterador;

import school.cesar.acadlab.dominio.historicoacademico.historico.RegistroDisciplina;
import java.util.List;

public class IteradorRegistrosDisciplina implements IteradorHistorico<RegistroDisciplina> {
    private final List<RegistroDisciplina> registros;
    private int posicao = 0;

    public IteradorRegistrosDisciplina(List<RegistroDisciplina> registros) {
        this.registros = registros;
    }

    @Override
    public boolean temProximo() {
        return posicao < registros.size();
    }

    @Override
    public RegistroDisciplina proximo() {
        if (!temProximo()) throw new IllegalStateException("Não há mais registros no histórico");
        return registros.get(posicao++);
    }
}
