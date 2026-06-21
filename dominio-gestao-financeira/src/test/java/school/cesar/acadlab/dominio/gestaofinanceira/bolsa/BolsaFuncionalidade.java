package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.evento.EventoObservador;

public class BolsaFuncionalidade {
    public final BolsaRepositorioFake repositorio = new BolsaRepositorioFake();
    public final EventoBarramento barramento = new BarramentoStub();
    public final BolsaServico servico = new BolsaServico(repositorio, barramento);
    public BolsaId ultimaBolsa;
    public RuntimeException excecao;

    static class BarramentoStub implements EventoBarramento {
        @Override public <E> void adicionar(EventoObservador<E> o) {}
        @Override public <E> void postar(E e) {}
    }
}
