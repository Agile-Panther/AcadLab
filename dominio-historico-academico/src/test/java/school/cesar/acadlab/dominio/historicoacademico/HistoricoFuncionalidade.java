package school.cesar.acadlab.dominio.historicoacademico;

public class HistoricoFuncionalidade {
    protected RepositorioEmMemoria repositorio;
    protected HistoricoAcademicoServico servico;

    public HistoricoFuncionalidade() {
        repositorio = new RepositorioEmMemoria();
        servico = new HistoricoAcademicoServico(repositorio);
    }
}
