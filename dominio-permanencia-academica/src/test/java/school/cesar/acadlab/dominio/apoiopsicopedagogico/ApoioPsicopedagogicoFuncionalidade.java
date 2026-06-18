package school.cesar.acadlab.dominio.apoiopsicopedagogico;

public class ApoioPsicopedagogicoFuncionalidade {
    protected Repositorio repositorio;
    protected EventoBarramentoEmMemoria eventoBarramento;
    protected ApoioServico apoioServico;
    protected TriagemServico triagemServico;
    protected AtendimentoServico atendimentoServico;
    protected AcaoPermanenciaServico acaoPermanenciaServico;
    protected ConsultaServico consultaServico;

    public ApoioPsicopedagogicoFuncionalidade() {
        repositorio = new Repositorio();
        eventoBarramento = new EventoBarramentoEmMemoria();
        apoioServico = new ApoioServico(repositorio, repositorio, eventoBarramento);
        triagemServico = new TriagemServico(repositorio, eventoBarramento);
        atendimentoServico = new AtendimentoServico(repositorio, eventoBarramento);
        acaoPermanenciaServico = new AcaoPermanenciaServico(repositorio);
        consultaServico = new ConsultaServico(repositorio);
    }
}
