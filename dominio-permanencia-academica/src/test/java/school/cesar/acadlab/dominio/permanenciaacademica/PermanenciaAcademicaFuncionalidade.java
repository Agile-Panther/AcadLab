package school.cesar.acadlab.dominio.permanenciaacademica;

public class PermanenciaAcademicaFuncionalidade {
    public RepositorioPermanencia repositorio;
    public EventoBarramentoEmMemoria eventoBarramento;
    public EditalServico editalServico;
    public InscricaoServico inscricaoServico;
    public BeneficioServico beneficioServico;
    public RuntimeException excecao;

    public PermanenciaAcademicaFuncionalidade() {
        repositorio = new RepositorioPermanencia();
        eventoBarramento = new EventoBarramentoEmMemoria();
        editalServico = new EditalServico(repositorio, eventoBarramento);
        inscricaoServico = new InscricaoServico(repositorio, repositorio, eventoBarramento);
        beneficioServico = new BeneficioServico(repositorio, repositorio, repositorio, eventoBarramento);
    }
}
