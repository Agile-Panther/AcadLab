package school.cesar.acadlab.dominio.permanenciaacademica;

public class PermanenciaAcademicaFuncionalidade {
    protected RepositorioPermanencia repositorio;
    protected EventoBarramentoEmMemoria eventoBarramento;
    protected EditalServico editalServico;
    protected InscricaoServico inscricaoServico;
    protected BeneficioServico beneficioServico;

    public PermanenciaAcademicaFuncionalidade() {
        repositorio = new RepositorioPermanencia();
        eventoBarramento = new EventoBarramentoEmMemoria();
        editalServico = new EditalServico(repositorio, eventoBarramento);
        inscricaoServico = new InscricaoServico(repositorio, repositorio, eventoBarramento);
        beneficioServico = new BeneficioServico(repositorio, repositorio, repositorio, eventoBarramento);
    }
}
