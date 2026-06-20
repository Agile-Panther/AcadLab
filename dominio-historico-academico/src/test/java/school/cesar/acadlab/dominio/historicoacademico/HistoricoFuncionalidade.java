package school.cesar.acadlab.dominio.historicoacademico;

public class HistoricoFuncionalidade {
    public RepositorioEmMemoria repositorio;
    public HistoricoAcademicoServico servico;
    public RuntimeException excecao;

    public HistoricoFuncionalidade() {
        repositorio = new RepositorioEmMemoria();
        servico = new HistoricoAcademicoServico(repositorio);
    }
}
