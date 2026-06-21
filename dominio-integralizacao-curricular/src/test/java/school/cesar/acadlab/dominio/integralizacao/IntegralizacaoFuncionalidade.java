package school.cesar.acadlab.dominio.integralizacao;

public class IntegralizacaoFuncionalidade {
    public IntegralizacaoRepositorioTest integralizacaoRepositorio;
    public ColacaoRepositorioTest colacaoRepositorio;
    public IntegralizacaoServico integralizacaoServico;
    public ColacaoServico colacaoServico;
    public ConsultaIntegralizacaoServico consultaServico;
    public RuntimeException excecao;

    public IntegralizacaoFuncionalidade() {
        integralizacaoRepositorio = new IntegralizacaoRepositorioTest();
        colacaoRepositorio = new ColacaoRepositorioTest();
        integralizacaoServico = new IntegralizacaoServico(integralizacaoRepositorio);
        colacaoServico = new ColacaoServico(colacaoRepositorio, integralizacaoRepositorio);
        consultaServico = new ConsultaIntegralizacaoServico(integralizacaoRepositorio, colacaoRepositorio);
    }
}
