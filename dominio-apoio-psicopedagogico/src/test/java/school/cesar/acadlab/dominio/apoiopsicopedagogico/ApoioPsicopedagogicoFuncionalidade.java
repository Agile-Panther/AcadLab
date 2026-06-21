package school.cesar.acadlab.dominio.apoiopsicopedagogico;

public class ApoioPsicopedagogicoFuncionalidade {
    public Repositorio repositorio;
    public EventoBarramentoEmMemoria eventoBarramento;
    public ApoioServico apoioServico;
    public TriagemServico triagemServico;
    public AtendimentoServico atendimentoServico;
    public AgendamentoServico agendamentoServico;
    public AcaoPermanenciaServico acaoPermanenciaServico;
    public ConsultaServico consultaServico;
    public RuntimeException excecao;

    public ApoioPsicopedagogicoFuncionalidade() {
        repositorio = new Repositorio();
        eventoBarramento = new EventoBarramentoEmMemoria();
        apoioServico = new ApoioServico(repositorio, repositorio, eventoBarramento);
        triagemServico = new TriagemServico(repositorio, eventoBarramento);
        atendimentoServico = new AtendimentoServico(repositorio, eventoBarramento);
        agendamentoServico = new AgendamentoServico(repositorio, eventoBarramento);
        acaoPermanenciaServico = new AcaoPermanenciaServico(repositorio);
        consultaServico = new ConsultaServico(repositorio);
    }
}
