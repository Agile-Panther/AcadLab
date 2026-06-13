package school.cesar.acadlab.dominio.ofertaturmas;

public class OfertaTurmasFuncionalidade {
    protected TurmaRepositorioTest turmaRepositorio;
    protected SalaRepositorioTest salaRepositorio;
    protected ProfessorRepositorioTest professorRepositorio;
    protected OfertaTurmaServico ofertaTurmaServico;
    protected ConsultaTurmaServico consultaTurmaServico;

    public OfertaTurmasFuncionalidade() {
        turmaRepositorio = new TurmaRepositorioTest();
        salaRepositorio = new SalaRepositorioTest();
        professorRepositorio = new ProfessorRepositorioTest();
        ofertaTurmaServico = new OfertaTurmaServico(turmaRepositorio, salaRepositorio, professorRepositorio);
        consultaTurmaServico = new ConsultaTurmaServico(turmaRepositorio);
    }
}
