package school.cesar.acadlab.dominio.ofertaturmas;

public class OfertaTurmasFuncionalidade {
    public TurmaRepositorioTest turmaRepositorio;
    public SalaRepositorioTest salaRepositorio;
    public ProfessorRepositorioTest professorRepositorio;
    public OfertaTurmaServico ofertaTurmaServico;
    public ConsultaTurmaServico consultaTurmaServico;
    public RuntimeException excecao;

    public OfertaTurmasFuncionalidade() {
        turmaRepositorio = new TurmaRepositorioTest();
        salaRepositorio = new SalaRepositorioTest();
        professorRepositorio = new ProfessorRepositorioTest();
        ofertaTurmaServico = new OfertaTurmaServico(turmaRepositorio, salaRepositorio, professorRepositorio);
        consultaTurmaServico = new ConsultaTurmaServico(turmaRepositorio);
    }
}
