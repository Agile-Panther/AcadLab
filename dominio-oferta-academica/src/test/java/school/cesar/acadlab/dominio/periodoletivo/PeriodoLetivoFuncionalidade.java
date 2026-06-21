package school.cesar.acadlab.dominio.periodoletivo;

public class PeriodoLetivoFuncionalidade {
    public Repositorio repositorio;
    public VerificadorPendenciasPeriodoStub verificadorPendencias;
    public VerificadorMatriculasPeriodoStub verificadorMatriculas;
    public PeriodoLetivoServico periodoLetivoServico;
    public ConsultaPeriodoLetivoServico consultaServico;
    public RuntimeException excecao;

    public PeriodoLetivoFuncionalidade() {
        repositorio = new Repositorio();
        verificadorPendencias = new VerificadorPendenciasPeriodoStub();
        verificadorMatriculas = new VerificadorMatriculasPeriodoStub();
        periodoLetivoServico = new PeriodoLetivoServico(repositorio, verificadorPendencias, verificadorMatriculas);
        consultaServico = new ConsultaPeriodoLetivoServico(repositorio);
    }

    public static class VerificadorPendenciasPeriodoStub implements VerificadorPendenciasPeriodo {
        private boolean pendencias = false;
        public void setPendencias(boolean pendencias) { this.pendencias = pendencias; }
        @Override
        public boolean possuiPendencias(PeriodoLetivoId periodoId) { return pendencias; }
    }

    public static class VerificadorMatriculasPeriodoStub implements VerificadorMatriculasPeriodo {
        private boolean matriculas = false;
        public void setMatriculas(boolean matriculas) { this.matriculas = matriculas; }
        @Override
        public boolean possuiMatriculasConfirmadas(PeriodoLetivoId periodoId) { return matriculas; }
    }
}
