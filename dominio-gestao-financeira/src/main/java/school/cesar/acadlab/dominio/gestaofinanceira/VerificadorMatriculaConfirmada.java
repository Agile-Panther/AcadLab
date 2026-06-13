package school.cesar.acadlab.dominio.gestaofinanceira;

public interface VerificadorMatriculaConfirmada {
    boolean possuiMatricula(EstudanteId estudanteId, PeriodoLetivoId periodoLetivoId);
}
