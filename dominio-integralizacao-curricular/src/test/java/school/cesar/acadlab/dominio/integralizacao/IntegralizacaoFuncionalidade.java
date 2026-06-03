package school.cesar.acadlab.dominio.integralizacao;

public class IntegralizacaoFuncionalidade {
    protected Repositorio repositorio;
    protected IntegralizacaoServico integralizacaoServico;
    protected ColacaoServico colacaoServico;
    protected ConsultaIntegralizacaoServico consultaServico;

    public IntegralizacaoFuncionalidade() {
        repositorio = new Repositorio();
        integralizacaoServico = new IntegralizacaoServico(repositorio);
        colacaoServico = new ColacaoServico(repositorio, repositorio);
        consultaServico = new ConsultaIntegralizacaoServico(repositorio, repositorio);
    }
}
