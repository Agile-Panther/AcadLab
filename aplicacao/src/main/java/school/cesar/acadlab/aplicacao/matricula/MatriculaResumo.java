package school.cesar.acadlab.aplicacao.matricula;

public record MatriculaResumo(
        int id,
        int estudanteId,
        int periodoLetivoId,
        String status
) {}
