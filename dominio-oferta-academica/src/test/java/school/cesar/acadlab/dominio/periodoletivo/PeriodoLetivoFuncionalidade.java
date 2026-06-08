package school.cesar.acadlab.dominio.periodoletivo;

public class PeriodoLetivoFuncionalidade {
    protected Repositorio repositorio;
    protected PeriodoLetivoServico periodoLetivoServico;
    protected ConsultaPeriodoLetivoServico consultaServico;

    public PeriodoLetivoFuncionalidade() {
        repositorio = new Repositorio();
        periodoLetivoServico = new PeriodoLetivoServico(repositorio);
        consultaServico = new ConsultaPeriodoLetivoServico(repositorio);
    }
}
