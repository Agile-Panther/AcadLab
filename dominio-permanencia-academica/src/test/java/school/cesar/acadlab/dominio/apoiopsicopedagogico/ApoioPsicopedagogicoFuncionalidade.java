package school.cesar.acadlab.dominio.apoiopsicopedagogico;

public class ApoioPsicopedagogicoFuncionalidade {
    protected Repositorio repositorio;
    protected ApoioServico apoioServico;
    protected TriagemServico triagemServico;
    protected AtendimentoServico atendimentoServico;
    protected AcaoPermanenciaServico acaoPermanenciaServico;
    protected ConsultaServico consultaServico;

    public ApoioPsicopedagogicoFuncionalidade() {
        repositorio = new Repositorio();
        apoioServico = new ApoioServico(repositorio, repositorio);
        triagemServico = new TriagemServico(repositorio);
        atendimentoServico = new AtendimentoServico(repositorio);
        acaoPermanenciaServico = new AcaoPermanenciaServico(repositorio);
        consultaServico = new ConsultaServico(repositorio);
    }
}
