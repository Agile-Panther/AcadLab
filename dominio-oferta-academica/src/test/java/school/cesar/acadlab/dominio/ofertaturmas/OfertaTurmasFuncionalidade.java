package school.cesar.acadlab.dominio.ofertaturmas;

public class OfertaTurmasFuncionalidade {
    protected Repositorio repositorio;
    protected OfertaTurmaServico ofertaTurmaServico;
    protected ConsultaTurmaServico consultaTurmaServico;

    public OfertaTurmasFuncionalidade() {
        repositorio = new Repositorio();
        ofertaTurmaServico = new OfertaTurmaServico(repositorio, repositorio, repositorio);
        consultaTurmaServico = new ConsultaTurmaServico(repositorio);
    }
}
