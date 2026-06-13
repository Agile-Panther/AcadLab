package school.cesar.acadlab.dominio.integralizacao;

public class IntegralizacaoFuncionalidade {
    protected IntegralizacaoRepositorioTest integralizacaoRepositorio;
    protected ColacaoRepositorioTest colacaoRepositorio;
    protected IntegralizacaoServico integralizacaoServico;
    protected ColacaoServico colacaoServico;
    protected ConsultaIntegralizacaoServico consultaServico;

    public IntegralizacaoFuncionalidade() {
        integralizacaoRepositorio = new IntegralizacaoRepositorioTest();
        colacaoRepositorio = new ColacaoRepositorioTest();
        integralizacaoServico = new IntegralizacaoServico(integralizacaoRepositorio);
        colacaoServico = new ColacaoServico(colacaoRepositorio, integralizacaoRepositorio);
        consultaServico = new ConsultaIntegralizacaoServico(integralizacaoRepositorio, colacaoRepositorio);
    }
}
