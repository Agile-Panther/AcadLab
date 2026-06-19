package school.cesar.acadlab.aplicacao.historicoacademico;

public record RegistroDisciplinaResumo(
        int id,
        int disciplinaId,
        int turmaId,
        int periodoLetivoId,
        double nota,
        double frequencia,
        String situacao) {
}
